set terminal term 
set output sprintf("%s/%s/%sk/%sm/cum_latency_fixed_kmob%s",outputdir,timestr,k,mob,termext)

set title sprintf("Cumulative distribution of 95th percentile latency for multiple runs\nk=%s, mob=%s, query=%s, duration=%s",k,mob,query,duration)
set xlabel "Latency (ms)"
set ylabel "Percentage of runs with latency < x"
set yrange [0:100]

set border linewidth 1.5
set style line 1 linewidth 2.5 linecolor rgb "red"
set style line 2 linewidth 2.5 linecolor rgb "blue"
set style line 3 linewidth 2.5 linecolor rgb "green"
set style line 4 linewidth 2.5 linecolor rgb "pink"
#set boxwidth 0.1
set style fill empty 

plot sprintf("%s/%s/%sk/%sm/cum-lat.data",outputdir,timestr,k,mob) using 2:1 notitle w lines linestyle 1
