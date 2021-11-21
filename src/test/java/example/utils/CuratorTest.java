package example.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class CuratorTest {
    @Autowired
    private CuratorFramework curatorFramework;

    @Test
    void createNodeTest() {
        String zkPath = "/test/CURD/node-1";
        byte[] playLoad = "hello zookeeper".getBytes(StandardCharsets.UTF_8);

        try {
            curatorFramework.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(zkPath, playLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void readNodeTest() {
        String zkPath = "/test/CURD/node-1";
        try {
            Stat stat = curatorFramework.checkExists().forPath(zkPath);
            if (stat != null) {
                byte[] playLoad = curatorFramework.getData().forPath(zkPath);
                String data = new String(playLoad, StandardCharsets.UTF_8);
                System.out.println(data);

                List<String> childPathList = curatorFramework.getChildren().forPath("/test");
                if (childPathList != null) {
                    for (String childPath : childPathList) {
                        System.out.println(childPath);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateNodeTest() {
        String zkPath = "/test/CURD/node-1";
        byte[] playLoad = "hello zookeeper again".getBytes(StandardCharsets.UTF_8);
        try {
            curatorFramework.setData().forPath(zkPath, playLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateNodeAsyncTest() {
        AsyncCallback.StatCallback callback = (i, s, o, stat) -> {
            System.out.println("i=" + i);
            System.out.println("s=" + s);
            System.out.println("o=" + o);
            System.out.println("stat=" + stat);
        };

        String zkPath = "/test/CURD/node-1";
        byte[] playLoad = "hello zookeeper async1".getBytes(StandardCharsets.UTF_8);

        ExecutorService threadPool = Executors.newFixedThreadPool(1);

        try {
            // 不设置线程池默认使用当前线程处理回调
            curatorFramework.setData().inBackground((this::asyncCallBack)).forPath(zkPath, playLoad);

            // 显示设置线程池处理回调
            curatorFramework.setData().inBackground((this::asyncCallBack), threadPool).forPath(zkPath, playLoad);
            System.out.println("update commit");
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void asyncCallBack(CuratorFramework client, CuratorEvent event) {
        System.out.println(Thread.currentThread().getName());
        System.out.println(event);
    }
}
