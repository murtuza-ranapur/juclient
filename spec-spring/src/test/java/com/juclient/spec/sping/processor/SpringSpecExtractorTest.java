package com.juclient.spec.sping.processor;

import com.jucient.spec.spring.processor.SpringSpecExtractor;
import com.juclient.core.parser.RequestType;
import com.juclient.core.parser.UnderstandableFunction;
import com.juclient.core.parser.UnderstandableRequestPeripherals;
import org.junit.jupiter.api.Test;
import org.test.pack.PagedResponseDTO;
import org.test.pack.StudentDTO;
import org.test.pack.StudentRequestDto;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpringSpecExtractorTest {
    @Test
    public void extract_api_annotation_present() {
        SpringSpecExtractor springSpecExtractor = new SpringSpecExtractor();
        List<UnderstandableFunction> functions = springSpecExtractor.extract("org.test.pack");

        assertEquals(5, functions.size());

        // Test each and every function
        Map<String, UnderstandableFunction> functionMap = functions.stream()
                .collect(Collectors.toMap(UnderstandableFunction::getFunctionName, val -> val));
        validateGetAllStudentsOverview(functionMap.get("getAllStudentsOverview"));
        validateAddStudent(functionMap.get("addStudent"));
        validateUpdateStudent(functionMap.get("updateStudent"));
        validateDeleteStudent(functionMap.get("deleteStudent"));
    }

    private void validateDeleteStudent(UnderstandableFunction deleteStudent) {
        assertEquals(1, deleteStudent.getPathParams().size());
        String path = deleteStudent.getPathParams().get(0);
        assertEquals("id", path);
        assertEquals(RequestType.DELETE, deleteStudent.getRequestType());
    }

    private void validateUpdateStudent(UnderstandableFunction updateStudent) {
        assertEquals(1, updateStudent.getRequestHeaders().size());
        UnderstandableRequestPeripherals requestPeripherals = updateStudent.getRequestHeaders().get(0);
        assertEquals("number", requestPeripherals.getName());
        assertEquals(false, requestPeripherals.getIsRequired());
    }

    private void validateAddStudent(UnderstandableFunction addStudent) {
        assertEquals(addStudent.getRequestParam().size(), 1);
        UnderstandableRequestPeripherals requestPeripherals = addStudent.getRequestParam().get(0);
        assertEquals(requestPeripherals.getName(), "yolo");
        assertEquals(addStudent.getRequestType(), RequestType.POST);
        assertEquals(addStudent.getRequestReturnType(), StudentDTO.class);
        assertEquals(addStudent.getRequestBodyType(), StudentRequestDto.class);
        assertEquals(addStudent.getUrl(), "/students");
    }

    private void validateGetAllStudentsOverview(UnderstandableFunction getAllStudentsOverview) {
        assertEquals(getAllStudentsOverview.getRequestParam().size(), 5);
        assertEquals(getAllStudentsOverview.getRequestType(), RequestType.GET);
        assertEquals(getAllStudentsOverview.getRequestReturnType().getTypeName(),
                "org.test.pack.PagedResponseDTO<java.util.List<org.test.pack.StudentCourseViewDTO>>");
        assertEquals(getAllStudentsOverview.getUrl(), "/students/overview");
    }
}
