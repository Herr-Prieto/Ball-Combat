package utils;

public class Constants {

	public static class Directions {

		public static final int LEFT  = 0;
		public static final int UP    = 1;
		public static final int RIGHT = 2;
		public static final int DOWN  = 3;
	}

	public static class PlayerConstants {

		// --- Estados de animacion ---
		public static final int IDLE      = 0;
		public static final int MOVING    = 1;
		public static final int ATTACKING = 2;
		public static final int JUMPING   = 3;
		public static final int HIT       = 4; // Para uso posterior (sin input manual)

		// ---------------------------------------------------------------
		//  MAPA DE FILAS  -  Spritesheet Player_01.png  (matriz [8][5])
		//
		//  Fila 0 -> IDLE      Derecha    cols 0-2  (3 frames)
		//  Fila 1 -> MOVING    Derecha    cols 0-3  (4 frames)
		//  Fila 2 -> IDLE      Izquierda  cols 0-2  (3 frames)
		//  Fila 3 -> MOVING    Izquierda  cols 0-3  (4 frames)
		//  Fila 4 -> ATTACKING Derecha    cols 0-4  (5 frames)
		//  Fila 5 -> ATTACKING Izquierda  cols 0-4  (5 frames)
		//  Fila 6 -> HIT       Derecha    cols 0-2  (3 frames)  [uso posterior]
		//  Fila 7 -> HIT       Izquierda  cols 0-2  (3 frames)  [uso posterior]
		//
		//  JUMPING reutiliza las filas visuales de MOVING (1 y 3),
		//  pero se mantiene como estado independiente en el codigo.
		// ---------------------------------------------------------------

		/**
		 * Devuelve la FILA del spritesheet para la accion y direccion dadas.
		 *
		 * @param action      IDLE | MOVING | JUMPING | ATTACKING | HIT
		 * @param facingRight true = mirando derecha, false = mirando izquierda
		 * @return indice de fila en el spritesheet
		 */
		public static int GetSpriteRow(int action, boolean facingRight) {
			switch (action) {
			case IDLE:
				return facingRight ? 0 : 2;
			case MOVING:
			case JUMPING: // Comparten filas visuales, pero son estados distintos
				return facingRight ? 1 : 3;
			case ATTACKING:
				return facingRight ? 4 : 5;
			case HIT:
				return facingRight ? 6 : 7;
			default:
				return 0;
			}
		}

		/**
		 * Devuelve cuantos frames tiene la animacion segun la accion.
		 *
		 * @param action IDLE | MOVING | JUMPING | ATTACKING | HIT
		 * @return numero de frames
		 */
		public static int GetSpriteAmount(int action) {
			switch (action) {
			case IDLE:
			case HIT:
				return 3;
			case MOVING:
			case JUMPING:
				return 4;
			case ATTACKING:
				return 5;
			default:
				return 1;
			}
		}
	}
}
