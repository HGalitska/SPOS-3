 class Process {
    int cputime;
    int arrivaltime;
    int ioblocking;
    int cpudone;
    int ionext;
    int numblocked;
    int numTickets;


   Process(int cputime, int ioblocking, int cpudone, int ionext, int numblocked, int numTickets) {
       this.cputime = cputime;
       this.ioblocking = ioblocking;
       this.cpudone = cpudone;
       this.ionext = ionext;
       this.numblocked = numblocked;
       this.numTickets = numTickets;
  } 	
}
