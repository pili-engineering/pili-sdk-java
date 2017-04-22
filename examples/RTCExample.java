
import java.util.Date;

public class RTCExample {
    public static void main(String[] args) {

        //init
        String accessKey = "DWQOcIddmtTCrnPp1ogwgdasdaAHBdIK1mIFrnmtnXb-66-";
        String secretKey = "cJFhYsuaaq7Vo35e1XDFUasdasG8Rm8C2VjkpmXO0aGkJGM";
        Client cli = new Client(accessKey, secretKey);
        Meeting meeting = cli.newMeeting();

        //create room
        try {
            String res = meeting.createRoom("123");
            System.out.println(res);
        } catch (PiliException e) {
            e.printStackTrace();
        }

        try {
            String res = meeting.createRoom("1234", "test");
            System.out.println(res);
        } catch (PiliException e) {
            e.printStackTrace();
        }

        try {
            String res = meeting.createRoom("12345", "testtest", 5);
            System.out.println(res);
        } catch (PiliException e) {
            e.printStackTrace();
        }

        //get room info
        try {
            Meeting.Room room = meeting.getRoom("testtest");
            System.out.println(room.name);
            System.out.println(room.status);
            System.out.println(room.ownerId);
            System.out.println(room.userMax);

        } catch (PiliException e) {
            e.printStackTrace();
        }

        //delete room
        try {
            meeting.deleteRoom("testtest");
        } catch (PiliException e) {
            e.printStackTrace();
        }

        //get roomToken
        try {
            String token = meeting.roomToken("test", "123456", "admin", new Date(System.currentTimeMillis() + 3600));
            System.out.println(token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}