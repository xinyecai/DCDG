package com.problem.dima;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import com.problem.basic.Problem;
import com.util.random.RandomGenerator;
import com.util.set.impl.KDTreeSet;
import com.util.set.inter.ExSetInter;
import com.util.solution.impl.DIMASolution;

public class DIMA_DEVProblem extends Problem<DIMASolution>{
	/**
	 * the number of devices
	 */
	private int devNum;
	/**
	 * the number of positions
	 */
	private int posNum;
	/**
	 * the number of resource kinds
	 */
	private int rscNum;
	/**
	 * the upper limit resource for each device
	 */
	private int [] devRscArray;
	/**
	 * the upper limit resource for each position
	 */
	private int [] posRscArray;
	/**
	 * the mass matrix
	 */
	private int [] MassMatrix;
	/**
	 * the OIC matrix
	 */
	private int [] OICMatrix;
	/**
	 * the segment array
	 */
	private int [] segArray;
	/**
	 * the KD-Tree set
	 */
	public KDTreeSet<DIMASolution> kdSet;
	/**
	 * the KD-Tree set as starting solutions
	 */
	public ExSetInter<DIMASolution> kdStartingSet;
	
	public DIMA_DEVProblem(){
		this.obj_num = 2;
	}
	
	@Override
	public void initialParameters() {
		try {
			String instanceFile = "files/instances/dima/testInstance.txt";
			BufferedReader br = new BufferedReader(new FileReader(instanceFile));
			String line = br.readLine();
			br.close();
			String [] arr = line.split("\\s++");
			this.sub_num = Integer.parseInt(arr[0]);
			this.devNum = Integer.parseInt(arr[1]);
			this.posNum = Integer.parseInt(arr[2]);
			this.rscNum = Integer.parseInt(arr[3]);
			this.devRscArray = new int[this.devNum*this.rscNum];
			this.posRscArray = new int[this.posNum*this.rscNum];
			this.MassMatrix = new int[this.devNum*this.posNum];
			this.OICMatrix = new int[this.devNum*this.posNum];
			this.segArray = new int[this.devNum*this.devNum];
			String fileHead = "files/instances/dima/"+devNum+"-"+posNum+"-";
			String dev_rsc_fileName = fileHead + "dev-rsc.txt";
			String pos_rsc_fileName = fileHead + "pos-rsc.txt";
			String mass_fileName = fileHead + "mass.txt";
			String oic_fileName = fileHead + "oic.txt";
			String seg_fileName = fileHead + "seg.txt";
			br = new BufferedReader(new FileReader(dev_rsc_fileName));
			int index = 0;
			for(line=br.readLine();line!=null;line=br.readLine()){
				this.devRscArray[index++] = Integer.parseInt(line.trim());
			}
			br.close();
			index = 0;
			br = new BufferedReader(new FileReader(pos_rsc_fileName));
			for(line=br.readLine();line!=null;line=br.readLine()){
				this.posRscArray[index++] = Integer.parseInt(line.trim());
			}
			br.close();
			index = 0;
			br = new BufferedReader(new FileReader(mass_fileName));
			for(line=br.readLine();line!=null;line=br.readLine()){
				this.MassMatrix[index++] = Integer.parseInt(line.trim());
			}
			br.close();
			index = 0;
			br = new BufferedReader(new FileReader(oic_fileName));
			for(line=br.readLine();line!=null;line=br.readLine()){
				this.OICMatrix[index++] = Integer.parseInt(line.trim());
			}
			br.close();
			index = 0;
			br = new BufferedReader(new FileReader(seg_fileName));
			for(int i=0;i<this.devNum*this.devNum;i++) this.segArray[i] = 0;
			int x, y;
			for(line=br.readLine();line!=null;line=br.readLine()){
				String [] dtArr = line.split("\\s++");
				x = Integer.parseInt(dtArr[0]);
				y = Integer.parseInt(dtArr[1]);
				x--;y--;
				this.segArray[x*devNum+y] = 1;
				this.segArray[y*devNum+x] = 1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * initialize population
	 */
	@Override
	public void initialPopulation(int pop_size) {
		for(int i=0;i<this.sub_num;i++){
			DIMASolution s = new DIMASolution(this.obj_num, -1);
			if(this.generateSolution(s)){
				this.calculateObjVal(s);
				this.kdSet.add(s);
				this.kdStartingSet.add(s);
				this.solutionArray[i] = s;
			}
		}
	}
	/**
	 * generate a valid solution sequence
	 * @param s
	 * @return
	 */
	public boolean generateSolution(DIMASolution s){
		int [] rand_array = RandomGenerator.permutation_array(0, this.devNum*this.posNum);
		int t_count = 0;
		for(int i=0;i<this.devNum;i++){
			int randDev = rand_array[i];
	        int [] randPosNum = RandomGenerator.permutation_array(0, this.posNum);
	        for(int j=0;j<posNum;j++){
	            int randPos = randPosNum[j];
	            if(!judgeRsc(s.allocated, randDev, randPos)
	               && !judgeSeg(s.sequence, randPos,randDev)){
	                s.sequence[randPos*devNum+randDev] = 1;
	                t_count++;
	                for(int k=0;k<rscNum;k++) s.allocated[k*posNum+randPos] += devRscArray[k*devNum+randDev];
	                break;
	            }
	        }
		}
		if(t_count==this.devNum) return true;
		return false;
	}
	/**
	 * calculate the object value
	 * @param s
	 */
	public void calculateObjVal(DIMASolution s){
		int totalMass = 0;
		for(int i=0;i<devNum*posNum;i++){
			totalMass += this.MassMatrix[i]*s.sequence[i];
		}
		int totalOIC = 0;
		for(int i=0;i<devNum*posNum;i++){
			totalOIC += this.OICMatrix[i]*s.sequence[i];
		}
		s.object_val[0] = totalMass;
		s.object_val[1] = totalOIC;
	}
	/**
	 * judge if there is a resource 
	 * @param allocated
	 * @param devIndex
	 * @param pos
	 * @return
	 */
	public boolean judgeRsc(int [] allocated, int devIndex, int pos){
		boolean ret = false;
		for(int i=0;i<this.rscNum;i++){
			if(allocated[i*this.posNum+pos]+this.devRscArray[i*this.devNum+devIndex]>this.posRscArray[i*this.posNum+pos]){
				ret = true;
				break;
			}
		}
		return ret;
	}
	/**
	 * judge if there is any segment conflict
	 * @param seq
	 * @param devIndex
	 * @return
	 */
	public boolean judgeSeg(int [] seq, int skip, int devIndex){
		 for(int i =0;i<devNum;i++){
		        if(seq[i]==0) continue;
		        if(this.segArray[skip*this.devNum + i * devNum + devIndex]==1){
		            return true;
		        }
		    }
		    return false;
	}
	/**
	 * release resource
	 * @param allocated
	 * @param devIndex
	 * @param posIndex
	 */
	public void releaseRSC(int [] allocated, int devIndex, int posIndex){
		for(int i=0;i<rscNum;i++){
	        allocated[posNum*i+posIndex] -= devRscArray[devNum*i+devIndex];
	    }
	}
}
