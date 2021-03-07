package org.j2s;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        User user = new User();
        user.setFirstname("Yevgeniy");
        user.setLastname("Tarassov");
        user.setAge(1000000);
        user.setNicknames(new String[] {"Xlv", "C0rnell", "Kashmir"});

        J2SCompilationType compilationType = J2SCompilationType.TYPESCRIPT;
        List<J2SCompilationResult> compilationResults = J2SCompiler.compile(User.class, compilationType);

        compilationResults.stream().map(J2SCompilationResult::getContent).forEach(System.out::println);

        J2SLibrary j2SLibrary = User.class.getAnnotation(J2SLibrary.class);
        J2SLibPublisher.publish(j2SLibrary.name(), j2SLibrary.version(), j2SLibrary.description(), compilationType, compilationResults);

        // todo messaging
    }
}
