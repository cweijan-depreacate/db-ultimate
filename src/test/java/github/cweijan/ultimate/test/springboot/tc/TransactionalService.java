package github.cweijan.ultimate.test.springboot.tc;

import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.test.bean.Lib;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionalService {

    @Transactional
    public void  test(){

        Query.of(Lib.class).update("test", "lib1").eq("id", 3).executeUpdate();
        if(true){
            throw new RuntimeException();
        }
        Query.of(Lib.class).update("test", "lib2").eq("id", 3).executeUpdate();

    }


}
