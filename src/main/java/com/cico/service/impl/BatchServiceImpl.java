package com.cico.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.cico.exception.ResourceNotFoundException;
import com.cico.model.Batch;
import com.cico.model.Course;
import com.cico.model.Student;
import com.cico.payload.ApiResponse;
import com.cico.payload.BatchRequest;
import com.cico.repository.BatchRepository;
import com.cico.repository.CourseRepository;
import com.cico.repository.StudentRepository;
import com.cico.repository.SubjectRepository;
import com.cico.repository.TechnologyStackRepository;
import com.cico.service.IBatchService;
import com.cico.util.AppConstants;


@Service
public class BatchServiceImpl implements IBatchService {
	
	public static final String BATCH_NOT_FOUND="BATCH NOT FOUND";
	public static final String BATCH_ADD_SUCCESS="Batch Created Successfully";
	public static final String BATCH_UPDATE_SUCCESS="Batch Update Successfully";
	
	@Autowired
	private BatchRepository batchRepository;
	
	@Autowired
	private TechnologyStackRepository repository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private StudentRepository studentRepository;
	
	
	@Override
	public ApiResponse createBatch(BatchRequest request) {
		Course course = courseRepository.findById(request.getCourseId()).orElseThrow(()-> new ResourceNotFoundException(AppConstants.NO_DATA_FOUND));
		Batch batch=new Batch(request.getBatchName(), request.getBatchStartDate(), request.getBatchTiming(), request.getBatchDetails());
		batch.setSubject(subjectRepository.findBySubjectIdAndIsDeleted(request.getSubjectId()).get());

		List<Batch> batches = course.getBatches();
		batches.add(batch);
		course.setBatches(batches);
		Course course2 = courseRepository.save(course);
		if(Objects.nonNull(course2))
			return new ApiResponse(Boolean.TRUE, BATCH_ADD_SUCCESS , HttpStatus.CREATED);
		return new ApiResponse(Boolean.FALSE,AppConstants.FAILED,HttpStatus.OK);
		
	}
	

	@Override
	public ApiResponse deleteBatch(Integer batchId) {
		Batch batch = batchRepository.findById(batchId).orElseThrow(()->new ResourceNotFoundException(BATCH_NOT_FOUND));
		batch.setDeleted(true);
		batchRepository.save(batch);	
		return new ApiResponse(Boolean.TRUE, AppConstants.DELETE_SUCCESS, HttpStatus.OK);
	}

	@Override
	public Batch getBatchById(Integer batchId) {
		Batch findByBatchIdAndIsDeleted = batchRepository.findByBatchIdAndIsDeleted(batchId,false);
		if(Objects.isNull(findByBatchIdAndIsDeleted)) {
			throw new ResourceNotFoundException(BATCH_NOT_FOUND);
		}
		return findByBatchIdAndIsDeleted;
	}

	@Override
	public List<Batch> getAllBatches(Integer studentId) {

	    Student student = studentRepository.findByStudentId(studentId);
	    Course currentCourse = student.getCourse();

	    List<Batch> batches = currentCourse.getBatches().stream()
	            .filter(batch -> !batch.isDeleted())
	            .collect(Collectors.toList());

	    if (batches.isEmpty()) {
	        throw new ResourceNotFoundException(BATCH_NOT_FOUND);
	    }

	    return batches;
	}


	@Override
	public List<Batch> getUpcomingBatches() {
		List<Batch> batches = batchRepository.findAllByBatchStartDate();
	 	if(batches.isEmpty())
	 		throw new ResourceNotFoundException(BATCH_NOT_FOUND);
	 	
	 	return batches;
	}

	@Override
	public ApiResponse updateBatchStatus(Integer batchId) {
	Batch batch = getBatchById(batchId);
		
		if(batch.isActive()==true)
			batch.setActive(false);
		
		else
			batch.setActive(true);
		
		batchRepository.save(batch);
		return new ApiResponse(Boolean.TRUE,AppConstants.SUCCESS,HttpStatus.OK);

	}


	@Override
	public ApiResponse updateBatch(Batch batch) {
		Batch save = batchRepository.save(batch);
		if(Objects.nonNull(save))
			return new ApiResponse(Boolean.TRUE, BATCH_UPDATE_SUCCESS, HttpStatus.CREATED);
		
		return new ApiResponse(Boolean.FALSE, AppConstants.FAILED, HttpStatus.OK);
	}

	

}
