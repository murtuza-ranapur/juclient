package com.juclient.core.specs;

import com.juclient.core.parser.Extractor;
import com.juclient.core.parser.RequestType;
import com.juclient.core.parser.UnderstandableFunction;
import com.juclient.core.parser.UnderstandableRequestPeripheral;
import com.juclient.core.specs.model.*;
import com.juclient.extra.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class SpecGeneratorTest {

    @Mock
    Extractor extractor;

    @Test
    public void generate_valid_input() {
        UnderstandableFunction getParent = new UnderstandableFunction();
        UnderstandableRequestPeripheral param = new UnderstandableRequestPeripheral();
        param.setName("id");
        param.setType(int.class);
        param.setIsRequired(true);
        getParent.setRequestParam(List.of(param));
        getParent.setClassName("com.juclient.extra");
        getParent.setRequestReturnType(ElementGrandParent.class);
        getParent.setUrl("/parent");
        getParent.setRequestType(RequestType.GET);
        getParent.setFunctionName("getParent");

        EndPoint getParentEp = new EndPoint();
        getParentEp.setGroup("com.juclient.extra");
        getParentEp.setUrl("/parent");
        getParentEp.setRequestType("GET");
        RequestPeripheral requestPeripheral = new RequestPeripheral();
        requestPeripheral.setName("id");
        requestPeripheral.setType("INTEGER");
        requestPeripheral.setRequired(true);
        getParentEp.getRequestParams().add(requestPeripheral);
        getParentEp.setReturnType("com.juclient.extra.ElementGrandParent");
        getParentEp.setSuggestedMethodName("getParent");

        UnderstandableFunction createParent = new UnderstandableFunction();
        createParent.setRequestBodyType(ElementGrandParent.class);
        createParent.setClassName("com.juclient.extra");
        createParent.setRequestReturnType(String.class);
        createParent.setUrl("/parent");
        createParent.setRequestType(RequestType.POST);
        createParent.setFunctionName("createParent");

        EndPoint createParentEp = new EndPoint();
        createParentEp.setGroup("com.juclient.extra");
        createParentEp.setUrl("/parent");
        createParentEp.setRequestType("POST");
        createParentEp.setRequestBodyType("com.juclient.extra.ElementGrandParent");
        createParentEp.setReturnType("STRING");
        createParentEp.setSuggestedMethodName("createParent");

        UnderstandableFunction searchPatentByname = new UnderstandableFunction();
        searchPatentByname.setPathParams(List.of("name"));
        searchPatentByname.setClassName("com.juclient.extra");
        searchPatentByname.setRequestReturnType(SampleController.class.getMethods()[1].getGenericReturnType());
        searchPatentByname.setUrl("/parent/search/{NAME}");
        searchPatentByname.setRequestType(RequestType.GET);
        searchPatentByname.setFunctionName("search");

        EndPoint searchParentEp = new EndPoint();
        searchParentEp.setGroup("com.juclient.extra");
        searchParentEp.setUrl("/parent/search/{NAME}");
        searchParentEp.setRequestType("GET");
        searchParentEp.getPathParams().add("name");
        searchParentEp.setReturnType("com.juclient.extra.Response([com.juclient.extra.ElementGrandParent])");
        searchParentEp.setSuggestedMethodName("search");

        UnderstandableEnum understandableEnum = new UnderstandableEnum("com.juclient.extra.Gender");
        understandableEnum.setValues(List.of("MALE", "FEMALE", "OTHER"));
        UnderstandableType childType = new UnderstandableType("com.juclient.extra.ElementChild");
        UnderstandableField gender = new UnderstandableField("gender", "com.juclient.extra.Gender");
        UnderstandableField name = new UnderstandableField("name", "STRING");
        childType.getFields().add(gender);
        childType.getFields().add(name);

        UnderstandableType parentType = new UnderstandableType("com.juclient.extra.ElementParent");
        UnderstandableField listChild = new UnderstandableField("children", "<com.juclient.extra.ElementChild>");
        UnderstandableField balance = new UnderstandableField("balance", "LONG");
        UnderstandableField height = new UnderstandableField("height", "FLOAT");
        parentType.getFields().add(balance);
        parentType.getFields().add(height);
        parentType.getFields().add(listChild);

        UnderstandableType grandParentType = new UnderstandableType("com.juclient.extra.ElementGrandParent");
        UnderstandableField listParent = new UnderstandableField("parents", "[com.juclient.extra.ElementParent]");
        UnderstandableField age = new UnderstandableField("age", "INTEGER");
        grandParentType.getFields().add(name);
        grandParentType.getFields().add(age);
        grandParentType.getFields().add(listParent);

        UnderstandableType response = new UnderstandableType("com.juclient.extra.Response");
        response.setParametrized(true);
        response.getParametrizedTypeNames().add("T");
        UnderstandableField size = new UnderstandableField("size", "INTEGER");
        UnderstandableField page = new UnderstandableField("page", "INTEGER");
        response.getFields().add(new UnderstandableField("data", "T"));
        response.getFields().add(size);
        response.getFields().add(page);

        Spec expectedSpec = new Spec();
        expectedSpec.setEndPoints(List.of(getParentEp, createParentEp, searchParentEp));
        expectedSpec.setTypes(List.of(grandParentType, parentType, childType, response));
        expectedSpec.setEnums(List.of(understandableEnum));

        List<UnderstandableFunction> understandableFunctions = List.of(getParent, createParent, searchPatentByname);
        when(extractor.extract("com.juclient.extra")).thenReturn(understandableFunctions);

        Spec spec = SpecGenerator.generate(extractor, "com.juclient.extra");
        assertIterableEquals(expectedSpec.getEndPoints(), spec.getEndPoints());
        assertContains(expectedSpec.getEnums(), spec.getEnums());
        assertContains(expectedSpec.getTypes(), spec.getTypes());
        assertEquals(expectedSpec.getVersion(), spec.getVersion());
        assertEquals(expectedSpec.getConfiguration(), spec.getConfiguration());
    }

    private void assertContains(List<?> expected, List<?> actual) {
        for (Object o : actual) {
            if (!expected.contains(o)) {
                fail("Expected value :" + o + " absent");
            }
        }
    }

    @Test
    public void generate_simple_object_valid() {
        UnderstandableFunction getParent = new UnderstandableFunction();
        UnderstandableRequestPeripheral param = new UnderstandableRequestPeripheral();
        param.setName("id");
        param.setType(int.class);
        param.setIsRequired(true);
        getParent.setRequestParam(List.of(param));
        getParent.setClassName("com.juclient.extra");
        getParent.setRequestReturnType(VerySimpleClass.class);
        getParent.setUrl("/parent");
        getParent.setRequestType(RequestType.GET);
        getParent.setFunctionName("getParent");

        EndPoint getParentEp = new EndPoint();
        getParentEp.setGroup("com.juclient.extra");
        getParentEp.setUrl("/parent");
        getParentEp.setRequestType("GET");
        RequestPeripheral requestPeripheral = new RequestPeripheral();
        requestPeripheral.setName("id");
        requestPeripheral.setType("INTEGER");
        requestPeripheral.setRequired(true);
        getParentEp.getRequestParams().add(requestPeripheral);
        getParentEp.setReturnType("com.juclient.extra.VerySimpleClass");
        getParentEp.setSuggestedMethodName("getParent");

        UnderstandableType verySimpleClass = new UnderstandableType("com.juclient.extra.VerySimpleClass");
        verySimpleClass.getFields().add(new UnderstandableField("age", "INTEGER"));
        verySimpleClass.getFields().add(new UnderstandableField("weight", "FLOAT"));
        verySimpleClass.getFields().add(new UnderstandableField("bankBalance", "DOUBLE"));
        verySimpleClass.getFields().add(new UnderstandableField("phone", "LONG"));
        verySimpleClass.getFields().add(new UnderstandableField("ageObject", "INTEGER"));
        verySimpleClass.getFields().add(new UnderstandableField("weightObject", "FLOAT"));
        verySimpleClass.getFields().add(new UnderstandableField("bankBalanceObject", "DOUBLE"));
        verySimpleClass.getFields().add(new UnderstandableField("phoneObject", "LONG"));
        verySimpleClass.getFields().add(new UnderstandableField("birthDate", "DATE"));
        verySimpleClass.getFields().add(new UnderstandableField("birthTime", "TIME"));
        verySimpleClass.getFields().add(new UnderstandableField("momBirthDate", "DATE"));
        verySimpleClass.getFields().add(new UnderstandableField("dateTime", "DATE"));

        Spec expectedSpec = new Spec();
        expectedSpec.setEndPoints(List.of(getParentEp));
        expectedSpec.setTypes(List.of(verySimpleClass));

        List<UnderstandableFunction> understandableFunctions = List.of(getParent);
        when(extractor.extract("com.juclient.extra")).thenReturn(understandableFunctions);

        Spec spec = SpecGenerator.generate(extractor, "com.juclient.extra");
        assertEquals(expectedSpec, spec);
    }

    @Test
    public void generate_compound_object_valid() {
        UnderstandableFunction getParent = new UnderstandableFunction();
        UnderstandableRequestPeripheral param = new UnderstandableRequestPeripheral();
        param.setName("id");
        param.setType(int.class);
        param.setIsRequired(true);
        getParent.setRequestParam(List.of(param));
        getParent.setClassName("com.juclient.extra");
        getParent.setRequestReturnType(VerySimpleCompoundClass.class);
        getParent.setUrl("/parent");
        getParent.setRequestType(RequestType.GET);
        getParent.setFunctionName("getParent");

        EndPoint getParentEp = new EndPoint();
        getParentEp.setGroup("com.juclient.extra");
        getParentEp.setUrl("/parent");
        getParentEp.setRequestType("GET");
        RequestPeripheral requestPeripheral = new RequestPeripheral();
        requestPeripheral.setName("id");
        requestPeripheral.setType("INTEGER");
        requestPeripheral.setRequired(true);
        getParentEp.getRequestParams().add(requestPeripheral);
        getParentEp.setReturnType("com.juclient.extra.VerySimpleCompoundClass");
        getParentEp.setSuggestedMethodName("getParent");

        UnderstandableType verySimpleClass = new UnderstandableType("com.juclient.extra.VerySimpleClass");
        verySimpleClass.getFields().add(new UnderstandableField("age", "INTEGER"));
        verySimpleClass.getFields().add(new UnderstandableField("weight", "FLOAT"));
        verySimpleClass.getFields().add(new UnderstandableField("bankBalance", "DOUBLE"));
        verySimpleClass.getFields().add(new UnderstandableField("phone", "LONG"));
        verySimpleClass.getFields().add(new UnderstandableField("ageObject", "INTEGER"));
        verySimpleClass.getFields().add(new UnderstandableField("weightObject", "FLOAT"));
        verySimpleClass.getFields().add(new UnderstandableField("bankBalanceObject", "DOUBLE"));
        verySimpleClass.getFields().add(new UnderstandableField("phoneObject", "LONG"));
        verySimpleClass.getFields().add(new UnderstandableField("birthDate", "DATE"));
        verySimpleClass.getFields().add(new UnderstandableField("birthTime", "TIME"));
        verySimpleClass.getFields().add(new UnderstandableField("momBirthDate", "DATE"));
        verySimpleClass.getFields().add(new UnderstandableField("dateTime", "DATE"));

        UnderstandableType verySimpleCompoundClass = new UnderstandableType(
                "com.juclient.extra.VerySimpleCompoundClass");
        verySimpleCompoundClass.getFields().add(new UnderstandableField("name", "STRING"));
        verySimpleCompoundClass.getFields()
                .add(new UnderstandableField("verySimpleClass", "com.juclient.extra.VerySimpleClass"));

        Spec expectedSpec = new Spec();
        expectedSpec.setEndPoints(List.of(getParentEp));
        expectedSpec.setTypes(List.of(verySimpleClass, verySimpleCompoundClass));

        List<UnderstandableFunction> understandableFunctions = List.of(getParent);
        when(extractor.extract("com.juclient.extra")).thenReturn(understandableFunctions);

        Spec spec = SpecGenerator.generate(extractor, "com.juclient.extra");
        assertEquals(expectedSpec, spec);
    }

    @Test
    public void generate_collection_valid() {
        UnderstandableFunction getParent = new UnderstandableFunction();
        UnderstandableRequestPeripheral param = new UnderstandableRequestPeripheral();
        param.setName("id");
        param.setType(int.class);
        param.setIsRequired(true);
        getParent.setRequestParam(List.of(param));
        getParent.setClassName("com.juclient.extra");
        getParent.setRequestReturnType(SimpleCollectionClass.class);
        getParent.setUrl("/parent");
        getParent.setRequestType(RequestType.GET);
        getParent.setFunctionName("getParent");

        EndPoint getParentEp = new EndPoint();
        getParentEp.setGroup("com.juclient.extra");
        getParentEp.setUrl("/parent");
        getParentEp.setRequestType("GET");
        RequestPeripheral requestPeripheral = new RequestPeripheral();
        requestPeripheral.setName("id");
        requestPeripheral.setType("INTEGER");
        requestPeripheral.setRequired(true);
        getParentEp.getRequestParams().add(requestPeripheral);
        getParentEp.setReturnType("com.juclient.extra.SimpleCollectionClass");
        getParentEp.setSuggestedMethodName("getParent");

        UnderstandableType understandableType = new UnderstandableType("com.juclient.extra.SimpleCollectionClass");
        understandableType.getFields().add(new UnderstandableField("names", "[STRING]"));
        understandableType.getFields().add(new UnderstandableField("codes", "<INTEGER>"));
        understandableType.getFields().add(new UnderstandableField("nameNickNameMap", "{STRING,STRING}"));
        understandableType.getFields().add(new UnderstandableField("onlyList", "[OBJECT]"));
        understandableType.getFields().add(new UnderstandableField("onlyMap", "{OBJECT,OBJECT}"));
        understandableType.getFields().add(new UnderstandableField("arrInt", "[INTEGER]"));

        Spec expectedSpec = new Spec();
        expectedSpec.setEndPoints(List.of(getParentEp));
        expectedSpec.setTypes(List.of(understandableType));

        List<UnderstandableFunction> understandableFunctions = List.of(getParent);
        when(extractor.extract("com.juclient.extra")).thenReturn(understandableFunctions);

        Spec spec = SpecGenerator.generate(extractor, "com.juclient.extra");
        assertEquals(expectedSpec, spec);

    }

    @Test
    public void generate_simple_enum_valid() {
        UnderstandableFunction getParent = new UnderstandableFunction();
        UnderstandableRequestPeripheral param = new UnderstandableRequestPeripheral();
        param.setName("id");
        param.setType(int.class);
        param.setIsRequired(true);
        getParent.setRequestParam(List.of(param));
        getParent.setClassName("com.juclient.extra");
        getParent.setRequestReturnType(VerySimpleEnumClass.class);
        getParent.setUrl("/parent");
        getParent.setRequestType(RequestType.GET);
        getParent.setFunctionName("getParent");

        EndPoint getParentEp = new EndPoint();
        getParentEp.setGroup("com.juclient.extra");
        getParentEp.setUrl("/parent");
        getParentEp.setRequestType("GET");
        RequestPeripheral requestPeripheral = new RequestPeripheral();
        requestPeripheral.setName("id");
        requestPeripheral.setType("INTEGER");
        requestPeripheral.setRequired(true);
        getParentEp.getRequestParams().add(requestPeripheral);
        getParentEp.setReturnType("com.juclient.extra.VerySimpleEnumClass");
        getParentEp.setSuggestedMethodName("getParent");

        UnderstandableType understandableType = new UnderstandableType("com.juclient.extra.VerySimpleEnumClass");
        understandableType.getFields().add(new UnderstandableField("gender", "com.juclient.extra.Gender"));

        UnderstandableEnum understandableEnum = new UnderstandableEnum("com.juclient.extra.Gender");
        understandableEnum.getValues().addAll(List.of("MALE", "FEMALE", "OTHER"));

        Spec expectedSpec = new Spec();
        expectedSpec.setEndPoints(List.of(getParentEp));
        expectedSpec.setTypes(List.of(understandableType));
        expectedSpec.setEnums(List.of(understandableEnum));

        List<UnderstandableFunction> understandableFunctions = List.of(getParent);
        when(extractor.extract("com.juclient.extra")).thenReturn(understandableFunctions);

        Spec spec = SpecGenerator.generate(extractor, "com.juclient.extra");
        assertEquals(expectedSpec, spec);
    }

    @Test
    public void generate_parametrized_valid() {
        UnderstandableFunction getParent = new UnderstandableFunction();
        UnderstandableRequestPeripheral param = new UnderstandableRequestPeripheral();
        param.setName("id");
        param.setType(int.class);
        param.setIsRequired(true);
        getParent.setRequestParam(List.of(param));
        getParent.setClassName("com.juclient.extra");
        getParent.setRequestReturnType(SimpleParametrizedClass.class.getDeclaredFields()[0].getGenericType());
        getParent.setUrl("/parent");
        getParent.setRequestType(RequestType.GET);
        getParent.setFunctionName("getParent");

        EndPoint getParentEp = new EndPoint();
        getParentEp.setGroup("com.juclient.extra");
        getParentEp.setUrl("/parent");
        getParentEp.setRequestType("GET");
        RequestPeripheral requestPeripheral = new RequestPeripheral();
        requestPeripheral.setName("id");
        requestPeripheral.setType("INTEGER");
        requestPeripheral.setRequired(true);
        getParentEp.getRequestParams().add(requestPeripheral);
        getParentEp.setReturnType("com.juclient.extra.Response([STRING])");
        getParentEp.setSuggestedMethodName("getParent");

        UnderstandableType responseType = new UnderstandableType("com.juclient.extra.Response");
        responseType.setParametrized(true);
        responseType.setParametrizedTypeNames(List.of("T"));
        responseType.getFields().add(new UnderstandableField("data", "T"));
        responseType.getFields().add(new UnderstandableField("size", "INTEGER"));
        responseType.getFields().add(new UnderstandableField("page", "INTEGER"));

        Spec expectedSpec = new Spec();
        expectedSpec.setEndPoints(List.of(getParentEp));
        expectedSpec.setTypes(List.of(responseType));

        List<UnderstandableFunction> understandableFunctions = List.of(getParent);
        when(extractor.extract("com.juclient.extra")).thenReturn(understandableFunctions);

        Spec spec = SpecGenerator.generate(extractor, "com.juclient.extra");
        assertEquals(expectedSpec, spec);
    }
}
