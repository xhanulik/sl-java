import driver.ATR;
import driver.ConfigureSmartcardCommand;
import driver.TargetController;

import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

public class Main {
    public static void main(String[] args) {
        TargetController target = null;
        try {
            target = new TargetController();
            target.open();
            if (target.isCardInserted())
                System.out.println("Card is inserted");
            else
                System.out.println("Card is NOT inserted");
            target.configureSmartcard(ConfigureSmartcardCommand.T.T1, 0, 0, true, true);
            ATR atr = target.getATR();
            System.out.printf("We are using protocol T=%d and the frequency of the ISO7816 clock is %d kHz !\n", atr.tProtocolCurr, atr.fMaxCurr / 1000);
            target.resetTriggerStrategy();
            target.setPreSendAPDUTriggerStrategy();

            CommandAPDU apdu = new CommandAPDU(0x00, 0xA4, 0x04, 0x00, new byte[]{0x12, 0x34, 0x56, 0x78, (byte)0x90, 0x01, 0x01});
            ResponseAPDU res = target.sendAPDU(apdu);
            if (res.getSW1() == 0x90 && res.getSW2() == 0x00)
                System.out.println("Response Success");
            target.close();
        } catch (Exception e) {
            if (target != null)
                target.close();
            System.out.println("Caught exception:");
            System.out.println(e.getMessage());
        }
        System.out.println("Done");
    }
}
