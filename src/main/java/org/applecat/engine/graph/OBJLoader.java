package org.applecat.engine.graph;

import org.applecat.engine.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * 加载3d对象
 */
public class OBJLoader {

    public static Mesh loadMesh(String fileName) throws Exception {
        List<String> lines = Utils.readAllLines(fileName);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    // Geometric vertex
                    Vector3f vec3f = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    vertices.add(vec3f);
                    break;
                case "vt":
                    // Texture coordinate
                    Vector2f vec2f = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]));
                    textures.add(vec2f);
                    break;
                case "vn":
                    // Vertex normal
                    Vector3f vec3fNorm = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3]));
                    normals.add(vec3fNorm);
                    break;
                case "f":
                    Face face = new Face(tokens[1], tokens[2], tokens[3]);
                    faces.add(face);
                    break;
                default:
                    // Ignore other lines
                    break;
            }
        }
        return reorderLists(vertices, textures, normals, faces);
    }

    /**
     * 将模型数据进行排序
     */
    private static Mesh reorderLists(List<Vector3f> vertices, List<Vector2f> textures, List<Vector3f> normals, List<Face> faces) {
        List<Integer> indieces = new ArrayList<>();

        // 创建传给mesh的数据数组
        float[] posArr = new float[vertices.size() * 3];
        float[] textColoredArr = new float[vertices.size() * 2];
        float[] normArr = new float[vertices.size() * 3];

        int i = 0;
        for (Vector3f pos : vertices) {
            posArr[i * 3] = pos.x;
            posArr[i * 3 + 1] = pos.y;
            posArr[i * 3 + 2] = pos.z;
            i++;
        }

        for (Face face : faces){
            for (IdxGroup indValue : face.getFaceVertexIndices()) {
                processFaceVertex(indValue, textures, normals, indieces, textColoredArr, normArr);
            }
        }

        int[] indicesArr = indieces.stream().mapToInt(v -> v).toArray();
        return new Mesh(posArr, textColoredArr, normArr, indicesArr);
    }

    /**
     * 将 一个面的一个点信息插入排序的数组中
     * @param indices 点的信息
     * @param textCoordList 所有坐标信息
     * @param normList 所有法线信息
     * @param indicesList 排好序的index数组
     * @param texCoordArr 排好序的纹理颜色数组
     * @param normArr 排好序的法线数组
     */
    private static void processFaceVertex(IdxGroup indices, List<Vector2f> textCoordList,
                                          List<Vector3f> normList, List<Integer> indicesList,
                                          float[] texCoordArr, float[] normArr) {
        int posIndex = indices.idxPos;
        indicesList.add(posIndex);
        
        if (indices.idxTextCoord >= 0){
            Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
            texCoordArr[posIndex * 2] = textCoord.x;
            texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y;
        }

        if (indices.idxVecNormal >= 0){
            Vector3f vecNorm = normList.get(indices.idxVecNormal);
            normArr[posIndex * 3] = vecNorm.x;
            normArr[posIndex * 3 + 1] = vecNorm.y;
            normArr[posIndex * 3 + 2] = vecNorm.z;
        }
    }

    protected static class Face {

        /**
         * List of idxGroup groups for a face triangle (3 vertices per face).
         */
        private final IdxGroup[] idxGroups; // 一个面包含3个点的数据

        public Face(String v1, String v2, String v3) {
            idxGroups = new IdxGroup[3];
            // Parse the lines
            idxGroups[0] = parseLine(v1);
            idxGroups[1] = parseLine(v2);
            idxGroups[2] = parseLine(v3);
        }

        private IdxGroup parseLine(String line) {
            IdxGroup idxGroup = new IdxGroup();

            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1;
            if (length > 1) {
                // It can be empty if the obj does not define text coords
                String textCoord = lineTokens[1];
                idxGroup.idxTextCoord = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : IdxGroup.NO_VALUE;
                if (length > 2) {
                    idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
                }
            }

            return idxGroup;
        }

        public IdxGroup[] getFaceVertexIndices() {
            return idxGroups;
        }
    }

    /**
     * 一个面中一个点的信息
     */
    protected static class IdxGroup {

        public static final int NO_VALUE = -1;

        public int idxPos; // 0, 顶点下标

        public int idxTextCoord; // 1, 贴图下标

        public int idxVecNormal; // 2, 法线下标

        public IdxGroup() {
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }
    }
}
