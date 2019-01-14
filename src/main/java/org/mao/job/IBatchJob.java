package org.mao.job;

import org.mao.job.bean.BaseDTO;

import java.util.List;

public interface IBatchJob<T> {

    public List<T> bunch();

    public void process(T t);
}
