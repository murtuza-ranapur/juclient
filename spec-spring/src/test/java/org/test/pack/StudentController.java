package org.test.pack;

import com.juclient.core.annotations.ApiClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
  @ApiClient
  @GetMapping
  public PagedResponseDTO<List<StudentDTO>> getAllStudents(
      @RequestParam(defaultValue = "0") Integer pageNo,
      @RequestParam(defaultValue = "2") Integer pageSize,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "true") boolean isAscending,
      @RequestParam(defaultValue = "") String search) {
    return null;
  }

  @ApiClient
  @RequestMapping(value = "/overview", method = {RequestMethod.GET, RequestMethod.POST})
  @GetMapping("/overview")
  public PagedResponseDTO<List<StudentCourseViewDTO>> getAllStudentsOverview(
      @RequestParam(defaultValue = "0") Integer pageNo,
      @RequestParam(defaultValue = "2") Integer pageSize,
      @RequestParam(defaultValue = "studentId") String sortBy,
      @RequestParam(defaultValue = "true") boolean isAscending,
      @RequestParam(defaultValue = "") String search) {
    return null;
  }

  @ApiClient
  @PostMapping
  public StudentDTO addStudent(@RequestBody @Valid StudentRequestDto studentDTO, @RequestParam("yolo") String id) {
    return null;
  }

  @ApiClient
  @PutMapping
  public StudentDTO updateStudent(@RequestBody @Valid StudentRequestDto studentDTO,
                                  @RequestHeader(required = false, name = "num", value = "1") Integer number) {
    return null;
  }

  @ApiClient
  @DeleteMapping("/{id}")
  public void deleteStudent(@PathVariable Long id) {

  }
}