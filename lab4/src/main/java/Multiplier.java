import org.javatuples.Pair;
import org.thavam.util.concurrent.blockingMap.BlockingHashMap;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

public class Multiplier implements Callable<Object> {
    private BlockingHashMap<Pair<Integer,Integer>,Integer> m1;
    private BlockingHashMap<Pair<Integer,Integer>,Integer> m2;
    private BlockingHashMap<Pair<Integer,Integer>,Integer> m_result;
    private List<Pair<Integer, Integer>> positions;

    Multiplier(BlockingHashMap<Pair<Integer,Integer>,Integer> m1, BlockingHashMap<Pair<Integer,Integer>,Integer> m2, BlockingHashMap<Pair<Integer,Integer>,Integer> m_result, List<Pair<Integer, Integer>> positions) {
        this.m1 = m1;
        this.m2 = m2;
        this.m_result = m_result;
        this.positions = positions;
    }


    @Override
    public Object call() throws Exception {
        positions.forEach(position -> {
            m_result.put(Pair.with(position.getValue0(),position.getValue1()),IntStream.range(0, Main.size1).map(i -> m1.get(Pair.with(position.getValue0(),i)) * m2.get(Pair.with(i,position.getValue1()))).sum());
        });
        return null;
    }
}
