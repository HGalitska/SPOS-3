// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Vector;
import java.io.*;

class SchedulingAlgorithm {

    static Results run(int quantum, int runtime, Vector<Process> processVector, Results result, int numberOfTickets, String resultsFile) {
        int comptime = 0;
        int currentProcess;
        int previousProcess;
        int size = processVector.size();
        int completed = 0;

        result.schedulingType = "Interactive (Preemptive)";
        result.schedulingName = "Lottery";

       try {

           PrintStream out = new PrintStream(new FileOutputStream(resultsFile));

           Lottery lottery = new Lottery(numberOfTickets);
           lottery.run(processVector);
           currentProcess = lottery.getWinner();

           Process process = processVector.elementAt(currentProcess);
           process.arrivaltime = comptime;
           out.println(comptime + ":     process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.arrivaltime + ")");

          while (comptime < runtime) {

              // if current process is done
              if (process.cpudone == process.cputime) {
                  completed++;
                  out.println(comptime + ":     process: " + currentProcess + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.arrivaltime + ")");

                  // if all processes are done
                  if (completed == size) {
                      result.compuTime = comptime;
                      out.close();
                      return result;
                  }

                  int i;
                  while (true) {
                      lottery.run(processVector); // run lottery to find out which process should run next
                      i = lottery.getWinner();
                      process = processVector.elementAt(i);
                      if (process.cpudone < process.cputime) {
                          currentProcess = i;
                          break;
                      }
                  }

                  process = processVector.elementAt(currentProcess);
                  if (process.cpudone == 0) process.arrivaltime = comptime;
                  out.println(comptime + ":     process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.arrivaltime + ")");
              }
              //BLOCKING ->


              if (process.ioblocking == process.ionext) {
                  out.println(comptime + ":     process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.arrivaltime + ")");
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
                  if (process.cpudone == 0) process.arrivaltime = comptime;
                  out.println(comptime + ":     process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.arrivaltime + ")");
              }

              process.cpudone++;

              if (process.ioblocking > 0) {
                  process.ionext++;
              }
              comptime++;

              // periodic lottery run
              System.out.println(currentProcess);
              System.out.println(process.cpudone % quantum == 0);
              if (process.cpudone % quantum == 0) {
                  lottery.run(processVector);
                  int i = lottery.getWinner();
                  process = processVector.elementAt(i);
                  if (process.cpudone < process.cputime) {
                      if (process.cpudone == 0) process.arrivaltime = comptime;
                      currentProcess = i;
                  }
                  process = processVector.elementAt(currentProcess);
                  out.println(comptime + ":     process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.arrivaltime + ")");
              }
          }

          out.close();
       }  catch (IOException e) { /* Handle exceptions */ }

       result.compuTime = comptime;
       return result;
  }
}
