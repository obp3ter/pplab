import org.javatuples.Pair;

import java.util.List;
import java.util.stream.IntStream;

public class Multiplier implements Runnable {
    private Integer[][] m1;
    private Integer[][] m2;
    private Integer[][] m_result;
    private List<Pair<Integer,Integer>> positions;

            Multiplier(Integer[][] m1, Integer[][] m2, Integer[][] m_result, List<Pair<Integer, Integer>> positions) {
        this.m1 = m1;
        this.m2 = m2;
        this.m_result = m_result;
        this.positions = positions;
    }

    @Override
    public void run() {
        positions.forEach(position -> {
            m_result[position.getValue0()][position.getValue1()]=IntStream.range(0, m1[0].length).map(i -> m1[position.getValue0()][i] * m2[i][position.getValue1()]).sum();
        });
    }
}
