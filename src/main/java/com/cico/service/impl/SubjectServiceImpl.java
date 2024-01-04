package com.cico.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cico.exception.ResourceAlreadyExistException;
import com.cico.exception.ResourceNotFoundException;
import com.cico.model.Chapter;
import com.cico.model.Course;
import com.cico.model.Subject;
import com.cico.payload.SubjectResponse;
import com.cico.repository.ChapterCompletedRepository;
import com.cico.repository.CourseRepository;
import com.cico.repository.StudentRepository;
import com.cico.repository.SubjectRepository;
import com.cico.repository.TechnologyStackRepository;
import com.cico.service.ISubjectService;
import com.cico.util.AppConstants;

@Service
public class SubjectServiceImpl implements ISubjectService {

	@Autowired
	private SubjectRepository subRepo;

	@Autowired
	private TechnologyStackRepository technologyStackRepository;

	@Autowired
	ChapterCompletedRepository chapterCompletedRepository;

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private CourseRepository courseRepository;

	@Override
	public ResponseEntity<?> addSubject(String subjectName, Integer imageId) {
		Map<String, Object> response = new HashMap<>();
		Subject subject = subRepo.findBySubjectNameAndIsDeleted(subjectName.trim());
		if (Objects.nonNull(subject))
			throw new ResourceAlreadyExistException("Subject already exist");

		subject = new Subject();
		subject.setSubjectName(subjectName.trim());
		subject.setTechnologyStack(technologyStackRepository.findById(imageId).get());
		
		Subject save = subRepo.save(subject);
		if (Objects.nonNull(save)) {
			response.put(AppConstants.MESSAGE, AppConstants.SUCCESS);
			response.put("subject", save);
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		}
		response.put(AppConstants.MESSAGE, AppConstants.FAILED);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public void addChapterToSubject(Integer subjectId, String chapterName) {
		Subject subject = subRepo.findBySubjectIdAndIsDeleted(subjectId)
				.orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

		List<Chapter> chapters = subject.getChapters();

		for (Chapter chapter : chapters) {
			if (chapter.getChapterName().trim().equals(chapterName.trim()))
				throw new ResourceAlreadyExistException(
						"Chapter: " + chapterName + " already exist in the Subject " + subject.getSubjectName());
		}
		Chapter obj = new Chapter();
		obj.setChapterName(chapterName);
		subject.getChapters().add(obj);
		subRepo.save(subject);
	}

	@Override
	public ResponseEntity<?> updateSubject(Subject subject) throws Exception {
		subRepo.findBySubjectIdAndIsDeleted(subject.getSubjectId())
				.orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

	    Subject sub = subRepo.findBySubjectNameAndIsDeleted(subject.getSubjectName().trim());
		if(Objects.nonNull(sub)) {
			throw new ResourceAlreadyExistException("Subject Already Present With This Name");
		}	
		Subject obj = subRepo.save(subject);
	obj.setChapters(obj.getChapters().stream().filter(obj1->obj1.getIsDeleted()==false).collect(Collectors.toList()));
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@Override
	public Map<String, Object> getSubjectById(Integer subjectId) {
		Subject subject = subRepo.findBySubjectIdAndIsDeleted(subjectId)
				.orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
		subject.setChapters(subject.getChapters().stream().filter(obj->obj.getIsDeleted()!=true).collect(Collectors.toList()));
		
		List<Chapter> chapters = subject.getChapters();
		long completedCount = chapters.stream().filter(Chapter::getIsCompleted).count();
		Map<String, Object> map = new HashMap<>();
		map.put("subject", subject);
		map.put("Chapter Count", subject.getChapters().size());
		map.put("Completed Chapter Count", completedCount);
		return map;
	}

	@Override
	public void deleteSubject(Integer subjectId) {
		Subject subject = subRepo.findBySubjectIdAndIsDeleted(subjectId)
				.orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
		subject.setIsDeleted(true);
		subRepo.save(subject);
	}

	@Override
	public void updateSubjectStatus(Integer subjectId) {
		Subject subject = subRepo.findBySubjectIdAndIsDeleted(subjectId)
				.orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

		if (subject.getIsActive().equals(true))
			subject.setIsActive(false);

		else
			subject.setIsActive(true);

		subRepo.save(subject);
	}

	@Override
	public List<SubjectResponse> getAllSubjects() {
		List<Subject> subjects = subRepo.findByIsDeletedFalse();
		List<SubjectResponse> responseSend = new ArrayList<>();
		for (Subject s : subjects) {
			SubjectResponse response = new SubjectResponse();
			response.setChapterCount(s.getChapters().stream().filter(obj->obj.getIsDeleted()==false).collect(Collectors.toList()).size()); // all chapter with isDeleted False
		//	response.setChapterCount(s.getChapters().size()); // all chapter with isDeleted False
			response.setTechnologyStack(s.getTechnologyStack());
			response.setIsActive(s.getIsActive());
			response.setIsDeleted(s.getIsDeleted());
			response.setSubjectId(s.getSubjectId());
			response.setSubjectName(s.getSubjectName());
			responseSend.add(response);
		}
		if (subjects.isEmpty())
			new ResourceNotFoundException("No subject available");
		return responseSend;
	}

	@Override
	public List<SubjectResponse> getAllSubjectsWithChapterCompletedStatus(Integer studentId) {
		Course course = studentRepository.findById(studentId).get().getCourse();
		List<Subject> subjects = courseRepository.findByCourseId(course.getCourseId()).get().getSubjects();
		List<Subject> list = subjects.stream().filter(obj ->obj.getIsDeleted()==false).collect(Collectors.toList());
		List<SubjectResponse> responseSend = new ArrayList<>();
		for (Subject s : list) {
			SubjectResponse response = new SubjectResponse();
			response.setChapterCount(s.getChapters().stream().filter(obj->obj.getIsDeleted()==false).collect(Collectors.toList()).size());
			response.setTechnologyStack(s.getTechnologyStack());
		    response.setIsActive(s.getIsActive());
			response.setIsDeleted(s.getIsDeleted());
			response.setSubjectId(s.getSubjectId());
			response.setSubjectName(s.getSubjectName());
			response.setChapterCompleted(chapterCompletedRepository.countBySubjectIdAndStudentId(s.getSubjectId(), studentId));
			responseSend.add(response);
		}
		if (subjects.isEmpty())
			new ResourceNotFoundException("No subject available");
		return responseSend;
	}
}
