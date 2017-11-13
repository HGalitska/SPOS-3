import java.util.Random;
import java.util.Vector;

class Lottery{
    private int numberOfTickets = 0;
    private int winner = -1;

    Lottery(int n) {
        numberOfTickets = n;
    }

    int getWinner() {
        return winner;
    }

    void run(Vector<Process> jobs) {
        Random rand = new Random();
        int ticket = rand.nextInt(numberOfTickets) + 1;
        int counter = 0;

        for (int i = 0; i < jobs.size(); i++){
            counter = counter + jobs.get(i).numTickets;
            if (counter >= ticket && jobs.get(i).cpudone < jobs.get(i).cputime){
                winner = i;
                break;
            }
        }
    }
}

//blocking, optimize lottery
