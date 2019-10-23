import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

public class Multiplier implements Callable<ArrayList<Pair<Pair<Integer,Integer>,Integer>>> {
    private Integer[][] m1;
    private Integer[][] m2;
    private Integer[][] m_result;
    private List<Pair<Integer, Integer>> positions;

    Multiplier(Integer[][] m1, Integer[][] m2, Integer[][] m_result, List<Pair<Integer, Integer>> positions) {
        this.m1 = m1;
        this.m2 = m2;
        this.m_result = m_result;
        this.positions = positions;
    }


    @Override
    public ArrayList<Pair<Pair<Integer,Integer>,Integer>> call() throws Exception {
        ArrayList<Pair<Pair<Integer,Integer>,Integer>> results = new ArrayList<>();
        positions.forEach(position -> results.add(Pair.with(Pair.with(position.getValue0(),position.getValue1()),IntStream.range(0, m1[0].length).map(i -> m1[position.getValue0()][i] * m2[i][position.getValue1()]).sum())));
        return results;
    }
}
