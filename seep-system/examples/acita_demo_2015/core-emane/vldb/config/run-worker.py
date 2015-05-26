#!/usr/bin/python

import subprocess,os,time,re,argparse,socket

master_port = 3500
eg_dir = os.path.dirname(os.path.realpath(__file__))
seep_jar = 'seep-system-0.0.1-SNAPSHOT.jar'
query_jar = 'acita_demo_2015.jar'
query_base = 'Base'
data_dir = '%s/log'%eg_dir

def main(k,h,query,w,hostname):
    sim_env = os.environ.copy()
    time_str = time.strftime('%H-%M-%a%d%m%y')

    try:
        print 'Waiting 5 seconds to start worker.'
        time.sleep(5)
        print 'Starting worker'
        worker_logfilename = wlog(w, k, query, hostname, time_str) 
        worker = start_worker(k, h, query, worker_logfilename, sim_env)

        print 'Waiting for any process to terminate.'
        while True:
            if not worker.poll() is None:
               break 

            time.sleep(0.5)

    finally:
        if worker:
            stop_worker(worker)

def start_worker(k, h, query, logfilename, sim_env):
    with open(data_dir+'/'+logfilename, 'w') as log:
        args = ['java', '-DuseCoreAddr=true','-DreplicationFactor=%d'%k,'-DchainLength=%d'%h,'-DqueryType=%s'%query, '-jar', '%s/../lib/%s'%(eg_dir, seep_jar), 'Worker']
        p = subprocess.Popen(args, stdout=log, stderr=subprocess.STDOUT, env=sim_env)
        return p

def stop_worker(worker):
    try:
        worker.terminate()
        print 'Stopped worker.'
    except:
        pass

def wlog(w, k, query, hostname, time_str):
    return 'worker-w%d-k%d-%s-%s-%s.log'%(w,k,query,hostname,time_str)

def read_k():
    with open('../k.txt', 'rb') as f:
        for line in f:
            return int(line.strip())

def read_h():
    with open('../h.txt', 'rb') as f:
        for line in f:
            return int(line.strip())

def read_query():
    with open('../query.txt', 'rb') as f:
        for line in f:
            return line.strip()

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Run simulations.')

    parser.add_argument('--k', dest='k', help='Number of replicas for each intermediate operator')
    parser.add_argument('--h', dest='h', help='Number of logical operators (chain)')
    parser.add_argument('--query', dest='query', help='Query type: chain, join')

    args=parser.parse_args()

    k = int(args.k) if args.k else read_k()
    h = int(args.h) if args.h else read_h()
    query = args.query if args.query else read_query() 
    w = 2 + (k*h)
    wname = socket.gethostname()
    
    main(k, h, query, w, wname)

