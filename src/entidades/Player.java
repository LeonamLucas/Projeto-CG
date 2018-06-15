package entidades;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import engine.DisplayManager;
import modelos.TexturedModel;

public class Player extends Entity {

	private static float RUN_SPEED = 50;
	private static float Acc = 0.5f; //aceleração
	public static int vida = 3;
	public static int points = 0;
	public static boolean movok = false; //Permissão de movimentação

	private float currentSpeed = 0;
	private float currentTurnSpeed = 0;

	public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}

	public void move() {
		checkInputs(); //Verifica os controles
		super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0); //Acrescenta as rotações ao Jogador
		float distance = currentSpeed * DisplayManager.getFrameTimeSeconds(); // Define a distância
		float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY()))); //Acrescenta a distância no eixo x a posição com base na rotação do eixo y
		float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));//Acrescenta a distância no eixo x a posição com base na rotação do eixo y
		super.increasePosition(dx, 0, dz); //Acrescenta as posições definidas.
		
		if(movok == false) {//verifica permissão de movimento
			currentSpeed = 0;
		}

	}

	public int getVida() {
		return vida;
	}

	public void setVida(int vida) {
		this.vida = vida;
	}
	
	public int getPoints() {
		return points;
	}

	public Entity checkTouch(List<Entity> entities, Player player) { //Verifica o contato com o jogador

		Entity removable = null; //Entidade a ser removida

		for (Entity entity : entities) {
			if (entity.getFlag() == true && entity.getFlag2() == false) {
				if (player.getPosition().z > entity.getPosition().z - 10
						&& player.getPosition().z < entity.getPosition().z + 10) {
					if (player.getPosition().x > entity.getPosition().x - 10
							&& player.getPosition().x < entity.getPosition().x + 10) {
						removable = entity;
						points++; // aumenta a pontuação
						System.out.println("Peixe, pontos: "+points);
						return removable;
					}
				}
			} else if (entity.getFlag() == true && entity.getFlag2() == true) {
				if (player.getPosition().z > entity.getPosition().z - 10
						&& player.getPosition().z < entity.getPosition().z + 10) {
					if (player.getPosition().x > entity.getPosition().x - 10
							&& player.getPosition().x < entity.getPosition().x + 10) {
						removable = entity;
						player.setVida(player.getVida() - 1); //diminui a vida
						System.out.println("Caixa, vida: "+player.getVida());
						return removable;
					}
				}
			}
		}

		return null;

	}

	private void checkInputs() { // Verifica os controles
		if (Keyboard.isKeyDown(Keyboard.KEY_W) && movok == true) {
			if (this.currentSpeed < RUN_SPEED) {
				this.currentSpeed += Acc; //Acrescenta a velocidade até o limite

			} else {
				this.currentSpeed = RUN_SPEED;
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_S) && movok == true) {
			if (this.currentSpeed > -RUN_SPEED) {
				this.currentSpeed -= Acc; //Diminui a velocidade até o limite
			} else {
				this.currentSpeed = -RUN_SPEED;
			}
		} else {
			if (this.currentSpeed > 0) {
				this.currentSpeed -= 0.3f;
			} else if (this.currentSpeed < 0) { //Reduz a velocidade caso nenhum botão seja pressionado
				this.currentSpeed += 0.3f;
			} else {
				this.currentSpeed = 0;
			}
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_D) && movok == true) {

			this.currentTurnSpeed = -super.getRotZ() * 200 * DisplayManager.getFrameTimeSeconds(); //Rotaciona o player
			if (super.getRotZ() < 15) {
				super.increaseRotation(0, 0, 50 * DisplayManager.getFrameTimeSeconds());//Inclina o barco
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_A) && movok == true) {

			this.currentTurnSpeed = -super.getRotZ() * 200 * DisplayManager.getFrameTimeSeconds(); // Rotaciona o player

			if (super.getRotZ() > -15) {
				super.increaseRotation(0, 0, -50 * DisplayManager.getFrameTimeSeconds());//Inclina o barco
			}
		} else {
			this.currentTurnSpeed = 0;

			if (super.getRotZ() < -1) { //Volta a inclinação para o padrão caso nenhum botão seja pressionado
				super.increaseRotation(0, 0, 50 * DisplayManager.getFrameTimeSeconds());
			} else if (super.getRotZ() > 1) {
				super.increaseRotation(0, 0, -50 * DisplayManager.getFrameTimeSeconds());
			} else {
				super.setRotZ(0);
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {//Turbo
			Acc = 2;
			RUN_SPEED = 600;

		} else {
			RUN_SPEED = 40;
			Acc = 0.5f;
		}

	}

}
