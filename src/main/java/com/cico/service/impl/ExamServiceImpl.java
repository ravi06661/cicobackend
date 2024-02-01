package com.cico.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cico.exception.ResourceAlreadyExistException;
import com.cico.exception.ResourceNotFoundException;
import com.cico.model.Chapter;
import com.cico.model.ChapterCompleted;
import com.cico.model.ChapterExamResult;
import com.cico.model.Exam;
import com.cico.model.Question;
import com.cico.model.Student;
import com.cico.payload.ChapterExamResultRequest;
import com.cico.payload.ChapterExamResultResponse;
import com.cico.payload.ExamResultResponse;
import com.cico.payload.QuestionResponse;
import com.cico.repository.ChapterCompletedRepository;
import com.cico.repository.ChapterExamResultRepo;
import com.cico.repository.ChapterRepository;
import com.cico.repository.ExamRepo;
import com.cico.repository.QuestionRepo;
import com.cico.repository.StudentRepository;
import com.cico.service.IExamService;
import com.cico.service.IFileService;
import com.cico.util.AppConstants;

@Service
public class ExamServiceImpl implements IExamService {

	@Autowired
	private ExamRepo examRepo;

	@Autowired
	ChapterRepository chapterRepo;

	@Autowired
	private QuestionRepo questionRepo;

	@Autowired
	private IFileService fileService;

	@Value("${fileUploadPath}")
	private String IMG_UPLOAD_DIR;
	@Autowired
	private ChapterExamResultRepo chapterExamResultRepo;

	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private StudentServiceImpl studentServiceImpl;

	@Autowired
	private ChapterCompletedRepository chapterCompletedRepository;

//	@Override
//	public void addExam(String examName) {
//		Exam exam = examRepo.findByExamNameAndIsDeleted(examName, false);
//
//		if (Objects.nonNull(exam))
//			throw new ResourceAlreadyExistException("Exam already exist");
//
//		exam = new Exam();
//		exam.setExamName(examName);
//		examRepo.save(exam);
//
//	}

	@Override
	public void addQuestionsToExam(Integer examId, String question, List<String> options, MultipartFile image) {
//		Exam exam = examRepo.findByExamIdAndIsDeleted(examId, false)
//				.orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
//
//		Question questionObj = questionRepo.findByQuestionContentAndIsDeleted(question, false);
//		if (Objects.nonNull(questionObj))
//			throw new ResourceAlreadyExistException("Question already exist");
//
//		questionObj = new Question(question, options);
//
//		if (image != null) {
//			questionObj.setQuestionImage(image.getOriginalFilename());
//			fileService.uploadFileInFolder(image, IMG_UPLOAD_DIR);
//		}
//
//		List<Question> questions2 = exam.getQuestions();
//
//		questions2.add(questionObj);
//
//		exam.setQuestions(questions2);
//
//		examRepo.save(exam);

	}

	@Override
	public void updateExam(Exam exam) {
		examRepo.findByExamIdAndIsDeleted(exam.getExamId(), false)
				.orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
		examRepo.save(exam);

	}

	@Override
	public Exam getExamById(Integer examId) {
		return examRepo.findByExamIdAndIsDeleted(examId, false)
				.orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
	}

	@Override
	public void deleteExam(Integer examId) {
		Exam exam = examRepo.findByExamIdAndIsDeleted(examId, false)
				.orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

		exam.setIsDeleted(true);
		examRepo.save(exam);

	}

	@Override
	public void updateExamStatus(Integer examId) {
		Exam exam = examRepo.findByExamIdAndIsDeleted(examId, false)
				.orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

		if (exam.getIsActive().equals(true))
			exam.setIsActive(false);

		else
			exam.setIsActive(true);

		examRepo.save(exam);

	}

	@Override
	public List<Exam> getAllExams() {
		List<Exam> exams = examRepo.findByIsDeleted(false);
		if (exams.isEmpty())
			new ResourceNotFoundException("No exam available");

		return exams;
	}

	@Override
	public List<Exam> getExamsByChapter(Integer chapterId) {
//		Chapter chapter = chapterRepo.findByChapterIdAndIsDeleted(chapterId, false)
//				.orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
//
//		if (chapter.getExams().isEmpty())
//			throw new ResourceNotFoundException("No exam available for Chapter : " + chapter.getChapterName());
//
//		return chapter.getExams();
		return null;

	}

	@Override
	public ResponseEntity<?> addChapterExamResult(ChapterExamResultRequest chapterExamResult) {
		Student student = studentRepository.findById(chapterExamResult.getStudentId()).get();
		Chapter chapter = chapterRepo.findById(chapterExamResult.getChapterId()).get();

		Optional<ChapterExamResult> findByChapterAndStudent = chapterExamResultRepo.findByChapterAndStudent(chapter,
				student);
		if (findByChapterAndStudent.isPresent())
			throw new ResourceAlreadyExistException("Your Are Already Submited This Test");

		ChapterExamResult examResult = new ChapterExamResult();
		Map<Integer, String> review = chapterExamResult.getReview();
		int correct = 0;
		int inCorrect = 0;
		examResult.setChapter(chapter);
		examResult.setStudent(student);

		List<Question> questions = chapter.getExam().getQuestions();
		questions = questions.parallelStream().filter(obj -> !obj.getIsDeleted()).collect(Collectors.toList());

		for (Question q : questions) {
//	    	if(!review.containsKey(q.getQuestionId())) {
//	    		 review.put(q.getQuestionId()," ");
//	    	}
			Integer id = q.getQuestionId();
			String correctOption = q.getCorrectOption();

			if (!review.isEmpty()) {
				String reviewAns = review.get(id);
				if (Objects.nonNull(reviewAns)) {
					if (review.get(id).equals(correctOption)) {
						correct++;
					} else {
						inCorrect++;
					}
				}
			}
		}
		examResult.setReview(review);
		examResult.setCorrecteQuestions(correct);
		examResult.setWrongQuestions(inCorrect);
		examResult.setNotSelectedQuestions(questions.size() - (correct + inCorrect));
		examResult.setScoreGet(correct - inCorrect);
		examResult.setTotalQuestion(questions.size());
		ChapterExamResult save = chapterExamResultRepo.save(examResult);

		ChapterCompleted chapterCompleted = new ChapterCompleted();
		chapterCompleted.setChapterId(chapterExamResult.getChapterId());
		chapterCompleted.setStudentId(chapterExamResult.getStudentId());
		chapterCompleted.setSubjectId(chapterExamResult.getSubjectId());
		chapterCompletedRepository.save(chapterCompleted);
		return new ResponseEntity<>(save, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getChapterExamResult(Integer id) {

		Map<String, Object> response = new HashMap<>();

		ChapterExamResult examResult = chapterExamResultRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.NO_DATA_FOUND));
		ChapterExamResultResponse chapterExamResultResponse = new ChapterExamResultResponse();

		chapterExamResultResponse.setCorrecteQuestions(examResult.getCorrecteQuestions());
		chapterExamResultResponse.setId(examResult.getId());
		chapterExamResultResponse.setNotSelectedQuestions(examResult.getNotSelectedQuestions());
		chapterExamResultResponse.setReview(examResult.getReview());
		chapterExamResultResponse.setWrongQuestions(examResult.getWrongQuestions());
		chapterExamResultResponse.setTotalQuestion(examResult.getTotalQuestion());
		chapterExamResultResponse.setScoreGet(examResult.getScoreGet());

		List<QuestionResponse> questions = examResult.getChapter().getExam().getQuestions().parallelStream()
				.map(obj -> questionFilter(obj)).collect(Collectors.toList());
		response.put("examResult", chapterExamResultResponse);
		response.put("questions", questions);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public QuestionResponse questionFilter(Question question) {
		QuestionResponse questionResponse = new QuestionResponse();
		questionResponse.setCorrectOption(question.getCorrectOption());
		questionResponse.setOption1(question.getOption1());
		questionResponse.setOption2(question.getOption2());
		questionResponse.setOption3(question.getOption3());
		questionResponse.setOption4(question.getOption4());
		questionResponse.setSelectedOption(question.getSelectedOption());
		questionResponse.setQuestionId(question.getQuestionId());
		questionResponse.setQuestionContent(question.getQuestionContent());
		questionResponse.setQuestionImage(question.getQuestionImage());

		return questionResponse;
	}

	@Override
	public ResponseEntity<?> getChapterExamIsCompleteOrNot(Integer chapterId, Integer studentId) {
		Map<String, Object> response = new HashMap<>();
		
		Optional<Chapter> chapterRes = chapterRepo.findById(chapterId);
		
		if(chapterRes.isPresent() && chapterRes.get().getExam()==null) {
			response.put(AppConstants.MESSAGE, AppConstants.EXAM_NOT_FOUND);
			return new ResponseEntity<>(response,HttpStatus.OK);
		}
		
		ChapterCompleted chapterCompleted = chapterCompletedRepository.findByChapterAndStudent(chapterId, studentId);
		Chapter chapter = chapterRepo.findByChapterIdAndIsDeleted(chapterId, false).get();
		Student student = studentRepository.findByStudentId(studentId);
		Optional<ChapterExamResult> examResult = chapterExamResultRepo.findByChapterAndStudent(chapter, student);
		response.put("chapterExamComplete", chapterCompleted);
		if (examResult.isPresent()) {
			response.put("resultId", examResult.get().getId());
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getChapterExamResultByChaterId(Integer chapterId) {
		Map<String, Object> response = new HashMap<>();
		List<ExamResultResponse> findAllById = chapterExamResultRepo.findAllStudentResultWithChapterId(chapterId);
		if (Objects.nonNull(findAllById)) {
			response.put("examResult", findAllById);
		} else {
			response.put(AppConstants.MESSAGE, AppConstants.NO_DATA_FOUND);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
