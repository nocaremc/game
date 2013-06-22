package nocare;

import nocare.StateMachine.GameState;

import org.lwjgl.input.Keyboard;

public class InputManager {

	public void handleInput() {
		if ( Keyboard.isKeyDown( App.getSettings().getKey( "key.exit" ) ) ) {
			App.endGame( false );
		}

		// If ingame
		if ( App.getcurrentState() == GameState.GAME ) {
			if ( Keyboard.isKeyDown( App.getSettings().getKey( "key.left" ) ) ) {
				App.getPlayer().setDX( -App.getPlayer().getStats().getRunSpeed() );
			}

			if ( Keyboard.isKeyDown( App.getSettings().getKey( "key.right" ) ) ) {
				App.getPlayer().setDX( App.getPlayer().getStats().getRunSpeed() );
			}

			if ( Keyboard.isKeyDown( App.getSettings().getKey( "key.jump" ) ) ) {
				App.getPlayer().setDX( 0 );
			}
		}

	}

}
