# Universal Client Generator

JUClient allows you to generate client code for your APIs in any client library of your choice. 

You use this, simply add `juclient` dependency is your project.

Then use `@ApiClient` annotations over any API you want in your client SDK.

```java
@RestController
@RequestMapping("/students")
public class StudentController {
   
   @ApiClient
   @GetMapping
   public PagedResponseDTO<List<StudentDTO>> getAllStudents(@RequestParam(defaultValue = "0") Integer pageNo,
           @RequestParam(defaultValue = "2") Integer pageSize, @RequestParam(defaultValue = "id") String sortBy,
           @RequestParam(defaultValue = "true") boolean isAscending, @RequestParam(defaultValue = "") String search) {
       return null;
   }

}
```

For instance if you are using `JUClient` with spring boot, just add `@ApiClient` annotation over your Mapping and it will 
generate the following SDK:

```java
|-Student-sdk
    |-src/java
        |-com.example.client
            |-StudentClient.java
        |-com.example.model
            |-PagedesponseDTO.java
            |-StudentDTO.java   
    |-pom.xml
```

And, the `StudentClient.java` will have FeignClient implementation for the same.

```java
@FiegnClient("my-app")
interface StudentClient{

    @RequestMapping(method= RequestMethod.GET, value="/students")
    PagedResponseDTO<List<StudentDTO>> getAllStudents(@RequestParam Integer pageNo,
               @RequestParam Integer pageSize, @RequestParam String sortBy,
               @RequestParam boolean isAscending, @RequestParam String search);
}
```
## Currently supported framework
1. Spring Boot
## Current Support for client
1. Feign Client

**Note**: This project is still work in progress
