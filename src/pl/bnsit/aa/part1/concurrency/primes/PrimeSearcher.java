package pl.bnsit.aa.part1.concurrency.primes;

import android.os.*;
import android.os.Process;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/20/13
 * Time: 11:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrimeSearcher {
    public static final int MESSAGE_FINISHED = 0;
    public static final int MESSAGE_FOUND_PRIME = 1;
    public static final int MESSAGE_UPDATE_NOT_PRIMES = 2;

    private final Handler messageBus;
    private Executor executor = Executors.newFixedThreadPool(5);

    private Set<Integer> alreadyNotPrimes = Collections.synchronizedSet(new HashSet<Integer>());
    private Set<Integer> primes = new HashSet<Integer>();
    private int maxValue;

    public PrimeSearcher(Handler messageBus, int maxValue) {
        this.messageBus = messageBus;
        this.maxValue = maxValue;
        executor.execute(new PrimeSearcherLoop());
    }

    private class PrimeSearcherLoop implements Runnable {

        private int primesCount;

        public PrimeSearcherLoop() {

        }

        @Override
        public void run() {
            notifyPrimeFound(1);
            for (int currentPrime = 2; currentPrime <= maxValue; ++currentPrime) {
                if (isNotAPrime(currentPrime))
                    continue;
                if (isPrime(currentPrime))
                    notifyPrimeFound(currentPrime);
                else
                    addNotPrime(currentPrime);

                removeMultiplesOf(currentPrime);
            }
            notifyFinished();
        }

        private void notifyFinished() {
            messageBus.obtainMessage(MESSAGE_FINISHED, primes.size(), alreadyNotPrimes.size() ).sendToTarget();
        }

        private void notifyPrimeFound(int prime) {
            primes.add(Integer.valueOf(prime));
            messageBus.obtainMessage(MESSAGE_FOUND_PRIME, prime, primes.size() ).sendToTarget();

        }

        private boolean isNotAPrime(int i) {
            return alreadyNotPrimes.contains(i);
        }
    }

    private void removeMultiplesOf(final int prime) {
        executor.execute(new RemoveMultiplesOfRunnable(prime));
    }

    private class RemoveMultiplesOfRunnable implements  Runnable {
        private final int prime;

        public RemoveMultiplesOfRunnable(int prime) {
            this.prime = prime;
        }

        @Override
        public void run() {
            if(Looper.getMainLooper() == Looper.myLooper()) {
               throw new RuntimeException();
            }
            int currentPrime = prime;
            while (currentPrime <= maxValue - prime) {
                currentPrime += prime;
                addNotPrime(currentPrime);
            }
            notifyFinished(prime);
        }

        private void notifyFinished(int prime) {
            messageBus.obtainMessage(MESSAGE_UPDATE_NOT_PRIMES, prime, alreadyNotPrimes.size() ).sendToTarget();
        }
    }

    private void addNotPrime(int currentPrime) {
        alreadyNotPrimes.add(currentPrime);
        messageBus.obtainMessage(MESSAGE_UPDATE_NOT_PRIMES, currentPrime, alreadyNotPrimes.size() ).sendToTarget();
    }


    private boolean isPrime(int possiblePrime) {
        for (int i = 2; i < possiblePrime; ++i) {
            if (possiblePrime % i == 0)
                return false;
        }

        return true;
    }}
