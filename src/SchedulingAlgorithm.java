// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Timer;
import java.util.Vector;
import java.io.*;

class SchedulingAlgorithm {

    static Results run(int runtime, Vector<Process> processVector, Results result, int numberOfTickets) {
        int comptime = 0;
        int currentProcess = 0;
        int previousProcess = 0;
        int size = processVector.size();
        int completed = 0;

        String resultsFile = "Summary-Processes";
        result.schedulingType = "Batch (Nonpreemptive)"; //Proportional-Share ?
        result.schedulingName = "Lottery";

       try {
           Lottery lottery = new Lottery(numberOfTickets);
           lottery.run(processVector);
           currentProcess = lottery.getWinner();

           PrintStream out = new PrintStream(new FileOutputStream(resultsFile));

           Process process = processVector.elementAt(currentProcess);
           out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + ")");

          while (comptime < runtime) {

              // if current process is done
              if (process.cpudone == process.cputime) {
                  completed++;
                  out.println("Process: " + currentProcess + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + ")");

                  // if all processes are done
                  if (completed == size) {
                      result.compuTime = comptime;
                      out.close();
                      return result;
                  }

                  int i;
                  while (true) {
                      lottery.run(processVector);
                      i = lottery.getWinner();
                      process = processVector.elementAt(i);
                      if (process.cpudone < process.cputime) {
                          currentProcess = i;
                          break;
                      }
                  }

                  process = processVector.elementAt(currentProcess);
                  out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + ")");
              }

              if (process.ioblocking == process.ionext) {
                  out.println("Process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + ")");
                  process.numblocked++;
                  process.ionext = 0;
                  previousProcess = currentProcess;

                  lottery.run(processVector);
                  int i = lottery.getWinner();
                  process = processVector.elementAt(i);
                  if (process.cpudone < process.cputime && previousProcess != i) {
                      currentProcess = i;
                  }

                  process = processVector.elementAt(currentProcess);
                  out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + ")");
              }
              process.cpudone++;

              if (process.ioblocking > 0) {
                process.ionext++;
              }
              comptime++;

          }

          out.close();
       }  catch (IOException e) { /* Handle exceptions */ }

       result.compuTime = comptime;
       return result;
  }
}
