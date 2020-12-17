package ua.lvivjavaclub.archunitdemo;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.Architectures;

import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "ua.lvivjavaclub.archunitdemo")
class ArchitectureTest {

  @ArchTest
  public static final ArchRule myRule = classes()
      .that().resideInAPackage("..service..")
      .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..");

  @Test
  void Services_should_only_be_accessed_by_Controllers() {
    ImportOption ignoreTests = new ImportOption() {
      @Override
      public boolean includes(Location location) {
        return !location.contains("/test/"); // ignore any URI to sources that contains '/test/'
      }
    };

    JavaClasses classes = new ClassFileImporter().withImportOption(ignoreTests).importClasspath();

    ArchRule myRule = classes()
        .that().resideInAPackage("..service..")
        .should().onlyBeAccessed().byAnyPackage("..controller..", "..service..");

    myRule.check(classes);
  }

  @Test
  void layeredArchitecture1() {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("ua.lvivjavaclub.archunitdemo");

    Architectures.LayeredArchitecture layeredArchitecture = layeredArchitecture()
        .layer("Controller").definedBy("..controller..")
        .layer("Service").definedBy("..service..")
        .layer("Persistence").definedBy("..repository..")

        .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
        .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
        .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service");

    layeredArchitecture.check(importedClasses);
  }


  @Test
  void customAnnotation() {
    JavaClasses importedClasses = new ClassFileImporter().importPackages("ua.lvivjavaclub.archunitdemo");


    DescribedPredicate<JavaClass> annotatedWith = new DescribedPredicate<JavaClass>("have a field annotated with @Payload") {
      @Override
      public boolean apply(JavaClass input) {
        return input.isAnnotatedWith(CustomAnnotation.class)
               &&
               input.getPackageName().startsWith("ua.lvivjavaclub.archunitdemo");
      }
    };
    
    ArchCondition<JavaClass> should = new ArchCondition<JavaClass>("only be accessed by @Secured methods") {
      @Override
      public void check(JavaClass item, ConditionEvents events) {

        CustomAnnotation annotationOfType = item.getAnnotationOfType(CustomAnnotation.class);

        if (annotationOfType.value().isBlank()) {
          String message = String.format(
              "Method %s is not @Secured", item);
          events.add(SimpleConditionEvent.violated(item, message));
        }
      }
    };

    ArchRule myRule = classes()
        .that(annotatedWith)
        .should(should);

    myRule.check(importedClasses);
  }
}

