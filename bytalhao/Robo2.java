package robo2;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

public class Robo2DefensorSniper extends AdvancedRobot {

    // Controla a direção do movimento
    private int direcao = 1;

    // Método principal do robô
    @Override
    public void run() {

        // Configuração das cores
        setBodyColor(Color.BLUE);
        setGunColor(Color.BLACK);
        setRadarColor(Color.YELLOW);
        setBulletColor(Color.RED);

        // Radar e arma independentes do movimento
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        // Inicia o radar
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);

        // Loop principal
        while (true) {
            execute();
        }
    }

    // Evento: encontrou um inimigo
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {

        // Calcula a posição absoluta do inimigo
        double anguloInimigo =
            getHeadingRadians() + e.getBearingRadians();

        // 1. RADAR TRAVADO NO ALVO
        double giroRadar =
            Utils.normalRelativeAngle(
                anguloInimigo - getRadarHeadingRadians()
            );

        setTurnRadarRightRadians(giroRadar * 2);

        // 2. MOVIMENTO EVASIVO
        double movimento =
            e.getBearingRadians() + (Math.PI / 2);

        setTurnRightRadians(
            Utils.normalRelativeAngle(movimento)
        );

        setAhead(150 * direcao);

        // 3. MIRA NO INIMIGO
        double giroArma =
            Utils.normalRelativeAngle(
                anguloInimigo - getGunHeadingRadians()
            );

        setTurnGunRightRadians(giroArma);

        // 4. POTÊNCIA DO TIRO POR DISTÂNCIA
        double potencia;

        if (e.getDistance() < 150) {
            potencia = 3.0;
        } else if (e.getDistance() < 400) {
            potencia = 2.0;
        } else {
            potencia = 1.0;
        }

        // Dispara contra o inimigo
        setFire(potencia);
    }

    // Evento: recebeu um tiro
    @Override
    public void onHitByBullet(HitByBulletEvent e) {

        // Inverte a direção para escapar
        direcao *= -1;

        setAhead(150 * direcao);

        // Muda a trajetória
        setTurnRight(45);
    }

    // Evento: bateu na parede
    @Override
    public void onHitWall(HitWallEvent e) {

        // Inverte o movimento
        direcao *= -1;

        // Afasta o robô da parede
        setBack(120);

        // Muda a direção
        setTurnRight(90);
    }
}
