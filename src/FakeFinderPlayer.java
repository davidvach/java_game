public class FakeFinderPlayer {
    public static Player findById(int id) {
        Player p = new Player();
        p.setId(id);
        p.setEmail("kunzo6@gmai.com");

        return p;
    }
}
