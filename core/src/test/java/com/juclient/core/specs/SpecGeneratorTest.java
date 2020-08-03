package com.juclient.core.specs;

import com.juclient.core.parser.Extractor;
import com.juclient.core.parser.RequestType;
import com.juclient.core.parser.UnderstandableFunction;
import com.juclient.core.parser.UnderstandableRequestPeripherals;
import com.juclient.extra.ElementGrandParent;
import com.juclient.extra.SampleController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
public class SpecGeneratorTest {

    @Mock
    Extractor extractor;

    @Test
    public void generate_valid_input() {
        UnderstandableFunction getParent = new UnderstandableFunction();
        UnderstandableRequestPeripherals param = new UnderstandableRequestPeripherals();
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
        RequestPeripherals requestPeripherals = new RequestPeripherals();
        requestPeripherals.setName("id");
        requestPeripherals.setType("INTEGER");
        getParentEp.getRequestParams().add(requestPeripherals);
        getParentEp.setReturnType("ElementGrandParent");
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
        createParentEp.setRequestBodyType("ElementGrandParent");
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
        searchParentEp.setUrl("/parent/search");
        searchParentEp.setRequestType("GET");
        searchParentEp.getPathParams().add("name");
        searchParentEp.setReturnType("Response(list(ElementGrandParent))");
        searchParentEp.setSuggestedMethodName("createParent");

        UnderstandableEnum understandableEnum = new UnderstandableEnum("Gender");

        UnderstandableType childType = new UnderstandableType("ElementChild");
        UnderstandableField gender = new UnderstandableField("gender","Gender");
        gender.setCoreType(false);
        UnderstandableField name = new UnderstandableField("name","STRING");
        childType.getFields().add(gender);
        childType.getFields().add(name);

        UnderstandableType parentType = new UnderstandableType("ElementParent");
        UnderstandableField listChild = new UnderstandableField("children","list(ElementChild)");
        listChild.setCoreType(false);
        UnderstandableField balance = new UnderstandableField("balance","LONG");
        UnderstandableField height = new UnderstandableField("height","FLOAT");
        parentType.getFields().add(balance);
        parentType.getFields().add(height);
        parentType.getFields().add(listChild);

        UnderstandableType grandParentType = new UnderstandableType("ElementGrandParent");
        UnderstandableField listParent = new UnderstandableField("parents","list(ElementParent)");
        UnderstandableField age = new UnderstandableField("age","INTEGER");
        grandParentType.getFields().add(name);
        grandParentType.getFields().add(age);
        grandParentType.getFields().add(listParent);

        UnderstandableType response = new UnderstandableType("Response");
        response.setParametrized(true);
        response.getParametrizedTypeNames().add("T");
        UnderstandableField size = new UnderstandableField("size","INTEGER");
        UnderstandableField page = new UnderstandableField("page","INTEGER");
        response.getFields().add(size);
        response.getFields().add(page);

        Spec expectedSpec = new Spec();
        expectedSpec.setEndPoints(List.of(getParentEp, createParentEp, searchParentEp));
        expectedSpec.setTypes(List.of(response, grandParentType, parentType, childType));
        expectedSpec.setEnums(List.of(understandableEnum));

        List<UnderstandableFunction> understandableFunctions = List.of(getParent, createParent, searchPatentByname);
        when(extractor.extract("com.juclient.extra")).thenReturn(understandableFunctions);

        Spec spec = SpecGenerator.generate(extractor, "com.juclient.extra");
        assertEquals(expectedSpec, spec);
    }
}
