package ch.realmtech.screen;

public enum ScreenType {
    LOADING(Loading.class),
    MENU(Menu.class),
    GAME_SCREEN(GameScreen.class);
    public final Class<? extends AbstractScreen> screenClass;

    ScreenType(Class<? extends AbstractScreen> screenClass){
        this.screenClass = screenClass;
    }
}
