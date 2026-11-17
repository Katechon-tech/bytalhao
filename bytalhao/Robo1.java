ackage grupo; //

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;

/**
 * Robo1 - "Caçador"
 * Papel no time: atacante agressivo. Trava o radar no inimigo mais próximo
 * e atira com potência proporcional à distância.
 * Nunca atira no parceiro (usa isTeammate).
 */
public class Robo1 extends TeamRobot {

    private int direcao = 1; // 1 = frente, -1 = trás

    @Override
    public void run() {
        setColors(Color.RED, Color.BLACK, Color.ORANGE);

        // Corpo, canhão e radar giram de forma independente
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while (true) {
            // Mantém o radar sempre girando até achar alguém
            if (getRadarTurnRemaining() == 0) {
                setTurnRadarRight(Double.POSITIVE_INFINITY);
            }
            setAhead(100 * direcao);
            setTurnRight(30 * direcao);
            execute();
        }
    }

    // ================= EVENTOS =================

    /** Viu algum robô */
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // Reconhecimento do parceiro: se for do time, ignora (não atira)
        if (isTeammate(e.getName())) {
            return;
        }

        double anguloAbsoluto = getHeading() + e.getBearing();

        // Trava o radar no inimigo
        double giroRadar = Utils.normalRelativeAngleDegrees(anguloAbsoluto - getRadarHeading());
        setTurnRadarRight(giroRadar * 2);

        // Mira o canhão
        double giroCanhao = Utils.normalRelativeAngleDegrees(anguloAbsoluto - getGunHeading());
        setTurnGunRight(giroCanhao);

        // Move-se perpendicular ao inimigo (dificulta ser acertado)
        setTurnRight(Utils.normalRelativeAngleDegrees(e.getBearing() + 90));

        // Potência do tiro conforme a distância
        double distancia = e.getDistance();
        double potencia;
        if (distancia < 150) {
            potencia = 3;
        } else if (distancia < 400) {
            potencia = 2;
        } else {
            potencia = 1;
        }
        // Economiza energia se estiver fraco
        if (getEnergy() < 20) {
            potencia = Math.min(potencia, 1);
        }

        // Só atira se o canhão já estiver quase alinhado e frio
        if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
            setFire(potencia);
        }
    }

    /** Tomou um tiro */
    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        // Inverte o sentido e faz uma curva para sair da linha de tiro
        direcao *= -1;
        setTurnRight(Utils.normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading())));
        setAhead(150 * direcao);
    }

    /** Bateu na parede */
    @Override
    public void onHitWall(HitWallEvent e) {
        direcao *= -1;
        setBack(100); // recua para longe da parede
        setTurnRight(90);
    }

    /** Colidiu com outro robô */
    @Override
    public void onHitRobot(HitRobotEvent e) {
        // Se for o parceiro: afasta-se e NÃO atira
        if (isTeammate(e.getName())) {
            direcao *= -1;
            setBack(80);
            setTurnRight(60);
            return;
        }

        // Inimigo: mira e dispara com força máxima
        double anguloAbsoluto = getHeading() + e.getBearing();
        double giroCanhao = Utils.normalRelativeAngleDegrees(anguloAbsoluto - getGunHeading());
        setTurnGunRight(giroCanhao);
        if (getGunHeat() == 0) {
            setFire(3);
        }
        // Dá uma recuada para não ficar preso
        setBack(50);
    }
}
