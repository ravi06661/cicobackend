package com.cico.service.impl;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cico.exception.ResourceNotFoundException;
import com.cico.model.Batch;
import com.cico.model.Course;
import com.cico.model.Student;
import com.cico.model.Subject;
import com.cico.payload.ApiResponse;
import com.cico.payload.CourseRequest;
import com.cico.payload.PageResponse;
import com.cico.repository.CourseRepository;
import com.cico.repository.StudentRepository;
import com.cico.repository.SubjectRepository;
import com.cico.repository.TechnologyStackRepository;
import com.cico.service.ICourseService;
import com.cico.util.AppConstants;

@Service
public class CourseServiceImpl implements ICourseService {

	@Autowired
	private CourseRepository courseRepository;
	@Autowired
	private TechnologyStackRepository repository;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private StudentRepository studentRepository;

	public static final String COURSE_ADD_SUCCESS = "Course Add Successfully"; 
	public static final String COURSE_NOT_FOUND = "Course Not Found"; 
	public static final String COURSE_UPDATE_SUCCESS = "Course Update Successfully"; 
	public static final String COURSE_UPGRADE_SUCCESS = "Course Update Successfully"; 
	
	@Override
	public ResponseEntity<?> createCourse(CourseRequest request) {
		Map<String, Object> response = new HashMap<>();
		Course course = new Course(request.getCourseName(), request.getCourseFees(), request.getDuration(), request.getSortDescription(), null,request.getIsStarterCourse());
		List<Subject> subjects = course.getSubjects();
		for (Integer id : request.getSubjectIds()) {
			subjects.add( subjectRepository.findBySubjectIdAndIsDeleted(id).get());
		}
		course.setSubjects(subjects);
		course.setCreatedDate(LocalDate.now());
		course.setUpdatedDate(LocalDate.now());
		course.setTechnologyStack(repository.findById(request.getTechnologyStack()).get());
		Course savedCourse = courseRepository.save(course);
		if(Objects.nonNull(savedCourse)){
			response.put(AppConstants.MESSAGE, COURSE_ADD_SUCCESS);
			response.put("course", savedCourse);
			return new ResponseEntity<>(response,HttpStatus.CREATED);
		}
		response.put(AppConstants.MESSAGE,AppConstants.FAILED);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}

	@Override
	public Course findCourseById(Integer courseId) {
	    // Find the course by courseId and ensure it's not deleted
	    Course course = courseRepository.findByCourseIdAndIsDeleted(courseId, false);

	    // Check if the course exists
	    if (course == null) {
	        throw new ResourceNotFoundException(COURSE_NOT_FOUND);
	    }

	    // Filter out batches that are not deleted
	    List<Batch> activeBatches = course.getBatches().stream()
	            .filter(batch -> !batch.isDeleted())
	            .collect(Collectors.toList());

	    List<Subject> activeSubject = course.getSubjects().stream()
	            .filter(subject -> !subject.getIsDeleted())
	            .collect(Collectors.toList());
	    
	    // Set the active batches in the course
	    course.setSubjects(activeSubject);
	    course.setBatches(activeBatches);
	    System.out.println(course);
	    return course;
	}

//	public Course findCourseById(Integer courseId) {
//		
//		Optional<Course> findById = courseRepository.findByCourseId(courseId);
//		if (!findById.isPresent()) {
//			throw new ResourceNotFoundException("Course is not found from given Id");
//		}
//
//		return findById.get();
//	}

	@Override
	public ResponseEntity<?> getAllCourses(Integer page, Integer size) {
	    if (page != -1) {
	        PageRequest p = PageRequest.of(page, size, Sort.by(Direction.DESC, "courseId"));
	        Page<Course> coursePageList = courseRepository.findAllByIsDeleted(false, p);
	        
	        // Fetch non-deleted batches for each course
	        List<Course> coursesWithBatches = new ArrayList<>();
	        for (Course course : coursePageList.getContent()) {
	            List<Batch> nonDeletedBatches = course.getBatches().stream()
	                    .filter(batch -> !batch.isDeleted())
	                    .collect(Collectors.toList());
	            List<Subject> nonDeletedSubjects = course.getSubjects().stream()
	                    .filter(subject -> !subject.getIsDeleted())
	                    .collect(Collectors.toList());
	            course.setSubjects(nonDeletedSubjects);
	            course.setBatches(nonDeletedBatches);
	            coursesWithBatches.add(course);
	        }
	        
	        PageResponse<Course> pageResponse = new PageResponse<>(
	            coursesWithBatches,
	            coursePageList.getNumber(),
	            coursePageList.getSize(),
	            coursePageList.getNumberOfElements(),
	            coursePageList.getTotalPages(),
	            coursePageList.isLast()
	        );
	        return new ResponseEntity<>(pageResponse, HttpStatus.OK);
	    } else {
	        List<Course> findAll = courseRepository.findByIsDeleted(false);
	        
	        // Fetch non-deleted batches for each course
	        List<Course> coursesWithBatches = new ArrayList<>();
	        for (Course course : findAll) {
	            List<Batch> nonDeletedBatches = course.getBatches().stream()
	                    .filter(batch -> !batch.isDeleted())
	                    .collect(Collectors.toList());
	            course.setBatches(nonDeletedBatches);
	            coursesWithBatches.add(course);
	        }
	        
	        return new ResponseEntity<>(coursesWithBatches, HttpStatus.OK);
	    }
	}

//	public ResponseEntity<?> getAllCourses(Integer page, Integer size) {
//		if(page != -1) {
//			PageRequest p = PageRequest.of(page, size, Sort.by(Direction.DESC, "courseId"));
//			 Page<Course> coursePageList = courseRepository.findAllByIsDeleted(false,p);
//			 PageResponse<Course> pageResponse = new PageResponse<>(coursePageList.getContent(), coursePageList.getNumber(), coursePageList.getSize(), coursePageList.getNumberOfElements(), coursePageList.getTotalPages(), coursePageList.isLast());
//			 return new ResponseEntity<>(pageResponse,HttpStatus.OK);
//		}else {
//			List<Course> findAll = courseRepository.findByIsDeleted(false);
//			 return new ResponseEntity<>(findAll,HttpStatus.OK);
//		}
//	}

	@Override
	public ApiResponse updateCourse(Course course) {
		
 	Course save = courseRepository.save(course);
 
		if(Objects.nonNull(save))
			return new ApiResponse(Boolean.TRUE, COURSE_UPDATE_SUCCESS, HttpStatus.CREATED);
		return new ApiResponse(Boolean.FALSE, AppConstants.FAILED, HttpStatus.OK);
	}

	@Override
	public Boolean deleteCourseById(Integer courseId) {
		int deleteCourse = courseRepository.deleteCourse(courseId);
		if(deleteCourse!=0)
		return true;
		return false;
	}

	@Override
	public ResponseEntity<?> getAllCourseApi(boolean isStarter) {
		List<Course> findAll = courseRepository.findAllByIsDeletedAndIsStarterCourse(false,isStarter);
		return  new ResponseEntity<>(findAll, HttpStatus.OK);
	}

	@Override
	public ApiResponse studentUpgradeCourse(Integer studnetId, Integer courseId) {
		  Student findByStudentId = studentRepository.findByStudentId(studnetId);
		  Optional<Course> findByCourseId = courseRepository.findByCourseId(courseId);
		  findByCourseId.get().setCourseFees(findByStudentId.getCourse().getCourseFees());
		  findByStudentId.setApplyForCourse(findByCourseId.get().getCourseName());
		  findByStudentId.setCourse(findByCourseId.get());
		  Student save = studentRepository.save(findByStudentId);
		  if(Objects.nonNull(save))
				return new ApiResponse(Boolean.TRUE, COURSE_UPGRADE_SUCCESS, HttpStatus.CREATED);
			return new ApiResponse(Boolean.FALSE, AppConstants.FAILED, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> getCourseProgress(Integer studentId) {
		Map<String, Object> response = new HashMap<>();
	    Student findByStudentId = studentRepository.findByStudentId(studentId);
	    String duration = findByStudentId.getCourse().getDuration();
	    System.out.println(duration);
	     long months = Long.parseLong(duration);

	    LocalDate joinDate = findByStudentId.getJoinDate();
	    LocalDate endDate = joinDate.plusMonths(months);
	    LocalDate currentDate = LocalDate.now();

	     // Calculate the number of days between join date and current date
        long daysElapsed = ChronoUnit.DAYS.between(joinDate, currentDate);

        // Calculate the total number of days between join date and end date
        long totalDays = ChronoUnit.DAYS.between(joinDate, endDate);

        // Calculate the percentage of completion
        double percentageCompletion = (double) daysElapsed / totalDays * 100;
  
//	    long monthsBetween = ChronoUnit.MONTHS.between(joinDate, currentDate);
//
//	    double percentage = (double) monthsBetween / durationValue * 100.0;
//
//	    // Ensure the percentage is within the range of 0% to 100%
//	    percentage = Math.min(percentage, 100.0);
//
//	    // Format the percentage to one decimal place
//	    DecimalFormat decimalFormat = new DecimalFormat("#.#");
//	    String formattedPercentage = decimalFormat.format(percentage);
        response.put("percentage", percentageCompletion>100?100:percentageCompletion);
        response.put("courseName", findByStudentId.getCourse().getCourseName());
        response.put("joinDate", joinDate);
        response.put("endDate", endDate);
        response.put("image", findByStudentId.getCourse().getTechnologyStack().getImageName());
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}


}


