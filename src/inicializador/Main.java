package inicializador;

//GRUPO: LEONAM LUCAS, MÁRCIO HENRIQUE E GABRIEL TELES

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import engine.DisplayManager;
import engine.Loader;
import engine.MasterRenderer;
import engine.OBJLoader;
import entidades.Camera;
import entidades.Entity;
import entidades.Light;
import entidades.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import modelos.RawModel;
import modelos.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import textures.ModelTexture;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

public class Main {

	public static void main(String[] args) {

		int tela = 0;

		DisplayManager.createDisplay(); //Cria o Display
		Loader loader = new Loader(); //Cria o loader para carregar elementos na tela

		List<Entity> entitiesGeral = new ArrayList<Entity>(); //Vetor de entidades

		ModelData barco = OBJFileLoader.loadOBJ("barco");	//Faz a leitura do arquivo .obj
		RawModel barcoModel = loader.loadToVAO(barco.getVertices(), barco.getTextureCoords(), barco.getNormals(), barco.getIndices()); //Monta o modelo 3d com base nas informações do .obj
		TexturedModel barcoTex = new TexturedModel(barcoModel, new ModelTexture(loader.loadTexture("barco"))); // Coloca textura no .obj
		
		Player player = new Player(barcoTex, new Vector3f(75, 10, -75), 0, 0, 0, 3);  // Cria o player(entidade)
		entitiesGeral.add(player); //Adiciona ao vetor de entidades
		Camera camera = new Camera(player); //Cria a camera e coloca o player como foco
		MasterRenderer renderer = new MasterRenderer(loader, camera); // Cria o renderizador.


		// ************ENTIDADES*******************

		ModelData peixe = OBJFileLoader.loadOBJ("salmao");
		RawModel peixeModel = loader.loadToVAO(peixe.getVertices(), peixe.getTextureCoords(), peixe.getNormals(),
				peixe.getIndices());
		TexturedModel peixeTex = new TexturedModel(peixeModel, new ModelTexture(loader.loadTexture("salmao")));

		ModelData box = OBJFileLoader.loadOBJ("box");
		RawModel boxModel = loader.loadToVAO(box.getVertices(), box.getTextureCoords(), box.getNormals(),
				box.getIndices());
		TexturedModel caixa = new TexturedModel(boxModel, new ModelTexture(loader.loadTexture("box")));

		ModelData ilha = OBJFileLoader.loadOBJ("boya");
		RawModel ilhaModel = loader.loadToVAO(ilha.getVertices(), ilha.getTextureCoords(), ilha.getNormals(),
				ilha.getIndices());
		TexturedModel island = new TexturedModel(ilhaModel, new ModelTexture(loader.loadTexture("boya")));
		Entity ilhaEn = new Entity(island, new Vector3f(0, 5, -75), 0, 0, 0, 20, false, false);
		entitiesGeral.add(ilhaEn);

		// *******************LUZ***************

		List<Light> lights = new ArrayList<Light>();
		Light sun = new Light(new Vector3f(50000, 100000, 60000), new Vector3f(1.3f, 1.3f, 1.3f));
		lights.add(sun);

		// ************************GUI****************************
		List<GuiTexture> guiMenu = new ArrayList<GuiTexture>(); //Vetor do menu
		List<GuiTexture> guivida = new ArrayList<GuiTexture>(); //Vetor da vida
		List<GuiTexture> guipts = new ArrayList<GuiTexture>(); //Vetor dos pontos
		
		
		
		

		for (int i = 0; i < player.getVida(); i++) { //For para adicionar os corações de acordo com a quantidade de vida.
			GuiTexture vidaGUI = new GuiTexture(loader.loadTexture("coracao"), new Vector2f((i / 10) - 0.9f, (i / 10f) - 0.9f), new Vector2f(0.05f, 0.05f)); //Carrega a textura do coração
			guivida.add(vidaGUI);// adiciona o coração ao vetor da vida
		}
		
		
		
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		// **********AGUA************************

		WaterFrameBuffers buffers = new WaterFrameBuffers();
		WaterShader waterShader = new WaterShader();
		WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		List<WaterTile> waters = new ArrayList<WaterTile>();
		WaterTile water = new WaterTile(75, -75, 6);
		waters.add(water);
		
		//******************COLETÁVEIS***********************
		int qntdEnemies = 300;
		int qntdColect = 5;
		Random random = new Random();
		for (int i = 0; i < qntdColect; i++) { // Cria os peixes em lugares aleatórios
			entitiesGeral
					.add(new Entity(peixeTex, new Vector3f(random.nextFloat() * 800 - 400, 5, random.nextFloat() * -600),
							0, random.nextInt(), 0, 20, true, false));
		}
		for (int i = 0; i < qntdEnemies; i++) { // Cria as caixas em lugares aleatórios
			entitiesGeral
					.add(new Entity(caixa, new Vector3f(random.nextFloat() * 800 - 400, 4, random.nextFloat() * -600),
							0, random.nextInt(), 0, 5, true, true));
		}

		// ****************LOOP DO JOGO*********************

		while (!Display.isCloseRequested()) {

			if (guivida.size() > player.getVida() && player.getVida() >= 0) {// Remove a vida do vetor da vida
				guivida.remove(player.getVida());
			}
			if(player.getVida() == 0) { //Muda o GUI em caso de derrrota
				sun.setColour(new Vector3f(1,0,0));
				tela = 6;
				player.movok = false;
			}
			if(player.getPoints() == qntdColect) { // Muda o GUI em caso de vitória
				sun.setColour(new Vector3f(0,1,0));
				tela = 5;
				player.movok = false;
			}

			for (int i = 0; i < player.getPoints(); i++) { //Adiciona os peixes ao vetor da vida
				GuiTexture ptsGUI = new GuiTexture(loader.loadTexture("peixe"),
						new Vector2f((i / 10) - 0.9f, ((i - player.getPoints()) / 10f) + 1f),
						new Vector2f(0.06f, 0.06f));
				guipts.add(ptsGUI);
			}

			player.move(); //Movimento do jogador
			camera.move(); //Movimento da camera

			renderer.renderShadowMap(entitiesGeral, sun); //Renderiza o mapa das sombras

			for (Entity entity : entitiesGeral) { //Renderiza as entidades
				renderer.processEntity(entity);
			}
			entitiesGeral.remove(player.checkTouch(entitiesGeral, player)); //Verifica o contato do jogador com as entidades

			GL11.glEnable(GL30.GL_CLIP_DISTANCE0); //Inicio renderização da água

			buffers.bindReflectionFrameBuffer();
			float distance = 2 * (camera.getPosition().y - water.getHeight());
			camera.getPosition().y -= distance;
			camera.invertPitch();
			renderer.renderScene(entitiesGeral, lights, camera,
					new Vector4f(0, 1, 0, -water.getHeight() + 1));
			camera.getPosition().y += distance;
			camera.invertPitch();

			buffers.bindRefractionFrameBuffer();
			renderer.renderScene(entitiesGeral, lights, camera,
					new Vector4f(0, -1, 0, water.getHeight()));

			GL11.glDisable(GL30.GL_CLIP_DISTANCE0); //Fim da renderização da água
			
			buffers.unbindCurrentFrameBuffer(); //"Limpa" o buffer para renderização
			renderer.renderScene(entitiesGeral, lights, camera, new Vector4f(0, -1, 0, 100000)); //renderiza os elementos com base na posição da camera
			waterRenderer.render(waters, camera, sun);//Renderiza a água
			guiRenderer.render(guivida);//Renderiza o GUI da vida
			guiRenderer.render(guipts);//renderiza o GUI da pontuação
		    sun.setAttenuation(new Vector3f(2,0,0));//Muda o brilho do sol
		    
		    //*******************LÓGICA DO MENU**************************
		    if(Keyboard.isKeyDown(Keyboard.KEY_P)) {
				System.out.println(player.getPosition().x+", "+player.getPosition().z);
			}
			
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				tela = 0;
			}

			if(tela == 0) {
				player.movok = false;
				guiMenu.clear();
				GuiTexture menu1 = new GuiTexture(loader.loadTexture("iniciar"), new Vector2f(0.25f,-0.25f), new Vector2f(1f, 1f));
				guiMenu.add(menu1);
				if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
					tela ++;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
					tela = 2;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
					tela = 4;
					player.movok = true;
					guiMenu.clear();
					player.vida = 3;
					sun.setColour(new Vector3f(1,1,1));
					
				}
			}else if(tela == 1) {
				guiMenu.clear();
				GuiTexture menu2 = new GuiTexture(loader.loadTexture("ajuda"), new Vector2f(0.25f,-0.25f), new Vector2f(1f, 1f));
				guiMenu.add(menu2);
				if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
					tela ++;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
					tela --;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
					tela = 3;
				}
			}else if(tela == 2) {
				guiMenu.clear();
				GuiTexture menu3 = new GuiTexture(loader.loadTexture("sair"), new Vector2f(0.25f,-0.25f), new Vector2f(1f, 1f));
				guiMenu.add(menu3);
				if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
					tela = 0;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
					tela --;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
					tela = 4;
					guiMenu.clear();
					buffers.cleanUp();
					waterShader.cleanUp();
					guiRenderer.cleanUp();
					renderer.cleanUp();
					loader.cleanUp();
					DisplayManager.closeDisplay();
					System.exit(0);
				}
			}else if(tela == 3) {
				guiMenu.clear();
				GuiTexture menu4 = new GuiTexture(loader.loadTexture("controles"), new Vector2f(0.25f,-0.25f), new Vector2f(1f, 1f));
				guiMenu.add(menu4);
				if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
					tela = 0;
				}
			}else if(tela == 4) {
				sun.setAttenuation(new Vector3f(1,0,0));
			}else if(tela == 5) {
				guiMenu.clear();
				GuiTexture menu6 = new GuiTexture(loader.loadTexture("ganhou"), new Vector2f(0.25f,-0.25f), new Vector2f(1f, 1f));
				guiMenu.add(menu6);
			}else if(tela == 6) {
				guiMenu.clear();
				GuiTexture menu7 = new GuiTexture(loader.loadTexture("perdeu"), new Vector2f(0.25f,-0.25f), new Vector2f(1f, 1f));
				guiMenu.add(menu7);
			}
			
			
			guiRenderer.render(guiMenu);

			DisplayManager.updateDisplay();//Atualiza o display
		}

		// *********LIMPEZA AO ENCERRAR**************

		buffers.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();//LIMPAM OS BUFFERS
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
