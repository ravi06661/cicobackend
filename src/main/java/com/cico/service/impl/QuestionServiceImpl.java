package com.cico.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cico.exception.ResourceAlreadyExistException;
import com.cico.exception.ResourceNotFoundException;
import com.cico.model.Chapter;
import com.cico.model.Exam;
import com.cico.model.Question;
import com.cico.repository.ChapterRepository;
import com.cico.repository.ExamRepo;
import com.cico.repository.QuestionRepo;
import com.cico.service.IFileService;
import com.cico.service.IQuestionService;
import com.cico.util.AppConstants;

@Service
public class QuestionServiceImpl implements IQuestionService {

	@Autowired
	private QuestionRepo questionRepo;

	@Autowired
	private IFileService fileService;

	@Autowired
	private ExamRepo examRepo;
	@Autowired
	private ChapterRepository chapterRepository;

	@Autowired
	private ChapterServiceImpl chapterServiceImpl;

	@Value("${fileUploadPath}")
	private String IMG_UPLOAD_DIR;

	@Override
	public Question addQuestion(Integer chapterId, String questionContent, String option1, String option2,
			String option3, String option4, MultipartFile image, String correctOption) {
		Question questionObj = questionRepo.findByQuestionContentAndIsDeleted(questionContent, false);
		if (Objects.nonNull(questionObj))
			throw new ResourceAlreadyExistException("Question already exist");

		questionObj = new Question();
		questionObj.setQuestionContent(questionContent);
		questionObj.setOption1(option1);
		questionObj.setOption2(option2);
		questionObj.setOption3(option3);
		questionObj.setOption4(option4);
		questionObj.setCorrectOption(correctOption);
		if (image != null) {
			questionObj.setQuestionImage(image.getOriginalFilename());
			String file = fileService.uploadFileInFolder(image, IMG_UPLOAD_DIR);
			questionObj.setQuestionImage(file);
		}

		Question save = questionRepo.save(questionObj);
		Chapter chapter = chapterRepository.findById(chapterId).get();
		Exam exam = chapter.getExam();
		exam.getQuestions().add(save);
		exam.setScore(exam.getQuestions().size());
		exam.setExamTimer(exam.getQuestions().size());
		examRepo.save(exam);
		return save;
	}

	@Override
	public ResponseEntity<?> updateQuestion(Integer questionId, String questionContent, String option1, String option2,
			String option3, String option4, String correctOption, MultipartFile image) {

		Map<String, Object> response = new HashMap<>();

		Question question = questionRepo.findByQuestionIdAndIsDeleted(questionId, false)
				.orElseThrow(() -> new ResourceNotFoundException("Question not found"));
		if (questionContent != null)
			question.setQuestionContent(questionContent);
		else
			question.setQuestionContent(question.getQuestionContent());
		if (option1 != null)
			question.setOption1(option1);
		else
			question.setOption1(question.getOption1());
		if (option2 != null)
			question.setOption2(option2);
		else
			question.setOption2(question.getOption2());
		if (option3 != null)
			question.setOption3(option3);
		else
			question.setOption3(question.getOption3());
		if (option4 != null)
			question.setOption4(option4);
		else
			question.setOption4(question.getOption4());

		if (correctOption != null)
			question.setCorrectOption(correctOption);
		else
			question.setCorrectOption(question.getCorrectOption());

		if (image != null && !image.isEmpty()) {
			if (image != null) {
				question.setQuestionImage(image.getOriginalFilename());
				String file = fileService.uploadFileInFolder(image, IMG_UPLOAD_DIR);
				question.setQuestionImage(file);
			}
		} else {
			question.setQuestionImage(question.getQuestionImage());
		}
		response.put(AppConstants.MESSAGE, AppConstants.UPDATE_SUCCESSFULLY);
		response.put("question", questionRepo.save(question));
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@Override
	public List<Question> getAllQuestionByChapterId(Integer chapterId) {
		Map<String, Object> chapter = chapterServiceImpl.getChapterById(chapterId);
		Chapter chapter1 = (Chapter) chapter.get("chapter");
		return chapter1.getExam().getQuestions();
	}

	@Override
	public void deleteQuestion(Integer questionId) {
		Question question = questionRepo.findByQuestionIdAndIsDeleted(questionId, false)
				.orElseThrow(() -> new ResourceNotFoundException("Question not found"));

		question.setIsDeleted(true);
		questionRepo.save(question);
	}

	@Override
	public void updateQuestionStatus(Integer questionId) {
		Question question = questionRepo.findByQuestionIdAndIsDeleted(questionId, false)
				.orElseThrow(() -> new ResourceNotFoundException("Question not found"));

		if (question.getIsActive().equals(true))
			question.setIsActive(false);
		else
			question.setIsActive(true);

		questionRepo.save(question);

	}

	@Override
	public List<Question> getAllQuestions() {
		List<Question> questions = questionRepo.findByIsDeleted(false);
		if (questions.isEmpty())
			new ResourceNotFoundException("No question available");

		return questions.parallelStream().filter(obj -> !obj.getIsDeleted()).collect(Collectors.toList());
	}

	@Override
	public List<Question> getQuestionsByExam(Integer examId) {
		Exam exam = examRepo.findByExamIdAndIsDeleted(examId, false)
				.orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

		if (exam.getQuestions().isEmpty())
			throw new ResourceNotFoundException("No question available for Exam : " + exam.getExamName());

		return exam.getQuestions();
	}

	@Override
	public Question getQuestionById(Integer questionId) {
		return this.questionRepo.findById(questionId)
				.orElseThrow(() -> new ResourceNotFoundException("Question not found with this id " + questionId));
	}

}
