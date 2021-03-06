package org.applecat.engine;

/**
 * 游戏引擎： 控制游戏的循环
 */
public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 75;

    public static final int TARGET_UPS = 30;

    private final IGameLogic gameLogic; // 游戏逻辑
    private final Window window; // 窗口 管理
    private final Timer timer;
    private final MouseInput mouseInput; // 鼠标事件

    public GameEngine(String windowTitle, int width, int height, boolean vsSync, IGameLogic gameLogic) {
        window = new Window(windowTitle, width, height, vsSync);
        this.gameLogic = gameLogic;
        timer = new Timer();
        mouseInput = new MouseInput();
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cleanup();
        }
    }

    protected void init() throws Exception{
        window.init();
        timer.init();
        gameLogic.init(window);
        mouseInput.init(window);
    }

    protected void gameLoop(){
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        boolean running = true;
        while (running && !window.windowShouldClose()){
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval){
                update(interval);
                accumulator -= interval;
            }

            render();

            if (!window.isvSync()){
                sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }

    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    protected void cleanup(){
        gameLogic.cleanup();
    }

    protected void render() {
        gameLogic.render(window);
        window.update();
    }
}