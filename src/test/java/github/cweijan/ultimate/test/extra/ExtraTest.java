package github.cweijan.ultimate.test.extra;

import github.cweijan.ultimate.annotation.extra.ExtraName;
import github.cweijan.ultimate.core.Query;
import github.cweijan.ultimate.test.base.BaseTest;
import org.junit.Test;

public class ExtraTest extends BaseTest {

    @Test
    public void testSaveExtra(){

        ImageEntry imageEntry = new ImageEntry();
        imageEntry.setName("test");
        imageEntry.setAge(20);
        Query.db.saveExtra(1,imageEntry);
        System.out.println(Query.db.getExtra(1,ImageEntry.class));

    }

    @Test
    public void testExprieExtra(){
        Query.db.expireExtra(1,ImageEntry.class,3);
    }

    @Test
    public void testGetExtra(){
        System.out.println(Query.db.getExtra(1,ImageEntry.class));
    }

    @ExtraName(value="imageEx",expireMinute = 30)
    static class ImageEntry{
        String name;
        Integer age;

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "ImageEntry{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }


}
