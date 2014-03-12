
data = dlmread('/home/ncarey/gitrepos/ParaProg/HW2/Parallel-DES-Key-Cracker-Benchmark/data/EC2/startupcost.txt',',')

xTicks = ['2^1^8';'2^1^9';'2^2^0';'2^2^1';'2^2^2';'2^2^3';'2^2^4']

keySize = data(:,1)
t1 = data(:,2)
t2 = data(:,3)
t4 = data(:,4)
t8 = data(:,5)
t16 = data(:,6)
t32 = data(:,7)

threads = [t1,t2,t4,t8,t16,t32]

plot(keySize, threads)

set(gca, 'Xscale', 'log')
set(gca, 'Xtick', keySize)
set(gca, 'Xticklabel', xTicks)
xlabel('Number of Keys examined (log scale)')

ylabel('Throughput: Keys Examined Per Second')

title('Startup Cost Examination: Throughput as a Function of Key-Space Size')
legend('1 thread','2 threads','4 threads','8 threads','16 threads','32 threads');
print -dpng /home/ncarey/gitrepos/ParaProg/HW2/Parallel-DES-Key-Cracker-Benchmark/data/graphs/Startup.png

