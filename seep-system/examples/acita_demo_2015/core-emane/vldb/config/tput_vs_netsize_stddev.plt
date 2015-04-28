set terminal pdf 
set output sprintf("%s/%s/tput_vs_netsize_stddev.pdf",outputdir,timestr)

set title "Throughput vs network size for different replication factors (k)"
set xlabel "Network size (nodes)"
set ylabel "Throughput (Kb/s)"
set yrange [0:*]
set xrange [20:50]

set style data histogram
#set style histogram cluster gap 1
#set style histogram errorbars gap 2 lw 1
set style histogram errorbars gap 1
set style fill solid 0.5 border 1.5
set boxwidth 0.9 absolute
#set bars front

set border linewidth 1.5
set style line 1 linewidth 2.5 linecolor rgb "red"
set style line 2 linewidth 2.5 linecolor rgb "blue"
set style line 3 linewidth 2.5 linecolor rgb "green"
set style line 4 linewidth 2.5 linecolor rgb "pink"
#set boxwidth 0.1
#set style fill empty 
set xtics ()

#plot sprintf("%s/%s/1k-tput.data",outputdir,timestr) using 2:4:xtic(1) title "k=1", \
#	sprintf("%s/%s/2k-tput.data",outputdir,timestr) using 2:4:xtic(1) title "k=2", \
#	sprintf("%s/%s/3k-tput.data",outputdir,timestr) using 2:4:xtic(1) title "k=3", \
#	sprintf("%s/%s/5k-tput.data",outputdir,timestr) using 2:4:xtic(1) title "k=5"

#plot sprintf("%s/%s/1k-tput.data",outputdir,timestr) using 1:2 title "k=1" w boxes linestyle 1, \
#	sprintf("%s/%s/2k-tput.data",outputdir,timestr) using 1:2 title "k=2" w boxes linestyle 2, \
#	sprintf("%s/%s/3k-tput.data",outputdir,timestr) using 1:2 title "k=3" w boxes linestyle 3, \
#	sprintf("%s/%s/5k-tput.data",outputdir,timestr) using 1:2 title "k=5" w boxes linestyle 4, \
#	sprintf("%s/%s/1k-tput.data",outputdir,timestr) using 1:2:4 notitle linestyle 1 with yerrorb, \
#	sprintf("%s/%s/2k-tput.data",outputdir,timestr) using ($1+0.05):2:4 notitle linestyle 2 with yerrorb, \
#	sprintf("%s/%s/3k-tput.data",outputdir,timestr) using ($1+0.10):2:4 notitle linestyle 3 with yerrorb, \
#	sprintf("%s/%s/5k-tput.data",outputdir,timestr) using ($1+0.15):2:4 notitle linestyle 4 with yerrorb


plot sprintf("%s/%s/1k-tput.data",outputdir,timestr) using ($1-1):2 title "k=1" w boxes ls 1, \
	sprintf("%s/%s/2k-tput.data",outputdir,timestr) using ($1+0):2 title "k=2" w boxes ls 2 , \
	sprintf("%s/%s/3k-tput.data",outputdir,timestr) using ($1+1):2 title "k=3" w boxes ls 3, \
	sprintf("%s/%s/5k-tput.data",outputdir,timestr) using ($1+2):2 title "k=5" w boxes ls 4, \
	sprintf("%s/%s/1k-tput.data",outputdir,timestr) using ($1-1):2:4 notitle w yerrorb ls 1, \
	sprintf("%s/%s/2k-tput.data",outputdir,timestr) using ($1+0):2:4 notitle w yerrorb ls 2, \
	sprintf("%s/%s/3k-tput.data",outputdir,timestr) using ($1+1):2:4 notitle w yerrorb ls 3, \
	sprintf("%s/%s/5k-tput.data",outputdir,timestr) using ($1+2):2:4 notitle w yerrorb 

#plot sprintf("%s/%s/1k-tput.data",outputdir,timestr) using ($1-1):2:4 title "k=1" w yerrorb, \
#	sprintf("%s/%s/2k-tput.data",outputdir,timestr) using ($1+0):2:4 title "k=2" w yerrorb, \
#	sprintf("%s/%s/3k-tput.data",outputdir,timestr) using ($1+1):2:4 title "k=3" w yerrorb, \
#	sprintf("%s/%s/5k-tput.data",outputdir,timestr) using ($1+2):2:4 title "k=5" w yerrorb 

