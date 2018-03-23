import org.flowable.app.extension.conf.BPMEngineConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nm.application.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class })
public class MyTest {

	@Autowired
	BPMEngineConfig bpmEngineConfig;

	@Test
	public void myMethod() {
		System.out.println(bpmEngineConfig.processEngine().getName());
		System.out.println(bpmEngineConfig.repositoryService().createDeployment()
		        .addClasspathResource("myprocess.bpmn20.xml").deploy());
	}
}
