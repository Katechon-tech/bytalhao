package bytalhao;
import robocode.*;

public class Robo2 extends TeamRobot {
    public void run() {
        while (true) {
            ahead(100);
            turnGunRight(360);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if (isTeammate(e.getName())) return;
        fire(1);
    }
}
