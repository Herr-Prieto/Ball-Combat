package utils;

public class Constants {
	
	
	public static class Directions{
		
		public static final int LEFT = 0;
		public static final int UP = 1;
		public static final int RIGHT = 2;
		public static final int DOWN = 3;
	}
	
	
	
	
	public static class PlayerConstants{
		
		
		public static final int IDLE = 0;
		public static final int MOVING = 1;
		public static final int ATTACKING = 2;
		public static final int JUMPING = 3;
		public static final int HIT = 4;
		
		
		
		public static int GetSpriteAmount(int player_Action) {
			
			switch(player_Action) {
			
			case IDLE:
			case MOVING:
			case HIT:
				return 3;
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
