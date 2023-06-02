package com.sevenme.wg;

import com.chromatic.common.utils.HttpContextUtils;
import com.chromatic.common.vo.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestWg {

    @Test
    public void test1() {

        String domain = HttpContextUtils.getDomain();
        System.out.println(domain);

        String origin = HttpContextUtils.getOrigin();
        System.out.println(origin);

        Result result = new Result();
        result.error(10001);

        System.out.println(result);
    }
}
