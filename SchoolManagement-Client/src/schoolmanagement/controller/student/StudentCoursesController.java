/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package schoolmanagement.controller.student;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import schoolmanagement.commonlib.communication.Operation;
import schoolmanagement.commonlib.communication.Request;
import schoolmanagement.commonlib.communication.Response;
import schoolmanagement.commonlib.communication.ResponseType;
import schoolmanagement.commonlib.model.Course;
import schoolmanagement.commonlib.model.Student;
import schoolmanagement.communication.Communication;
import schoolmanagement.session.Session;
import schoolmanagement.view.component.StudentCourseSelectionModel;
import schoolmanagement.view.student.StudentCoursesView;

/**
 *
 * @author ivano
 */
public class StudentCoursesController {

    private final StudentCoursesView coursesView;
    private final StudentCourseSelectionModel model;
    private List<Course> courses;
    private final Student student;

    public StudentCoursesController() {
        student = (Student) Session.getInstance().get("user");
        coursesView = new StudentCoursesView();
        coursesView.setVisible(true);
        initView();
        model = (StudentCourseSelectionModel) coursesView.getTblCourses().getModel();
    }

    private void initView() {
        initListeners();
        populateTable();
    }

    private void initListeners() {
        coursesView.getLblHome().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new StudentHomeController();
                coursesView.dispose();
            }
        });

        coursesView.getBtnSort().addActionListener(e -> sortCourses());
        coursesView.getBtnSearch().addActionListener(e -> searchCourses());
        coursesView.getBtnChoose().addActionListener(e -> chooseCourses());
    }

    private void populateTable() {
        courses = getStudentsUnselectedCourses();
        coursesView.getTblCourses().setModel(new StudentCourseSelectionModel(courses));
    }

    private void sortCourses() {
        List<Course> temp = this.courses;
        if(coursesView.getJrbLevel().isSelected()){
            temp = temp.stream().sorted((c1,c2)-> c1.getLanguage().getLevel().toString().compareTo(c2.getLanguage().getLevel().toString())).collect(Collectors.toList());
        }
        if(coursesView.getJrbLanguage().isSelected()){
            temp = temp.stream().sorted((c1,c2)-> c1.getLanguage().getName().compareTo(c2.getLanguage().getName())).collect(Collectors.toList());
        }
        if (coursesView.getJrbStartDate().isSelected()) {
            temp = temp.stream().sorted(Comparator.comparing(Course::getStartDate)).collect(Collectors.toList());
        }
        if(coursesView.getJrbEndDate().isSelected()){
            temp = temp.stream().sorted(Comparator.comparing(Course::getEndDate)).collect(Collectors.toList());
        }
        model.setCourses(temp);
    }

    private void searchCourses() {

    }

    private void chooseCourses() {

    }

    private List<Course> getStudentsUnselectedCourses() {
        List<Course> tempCourses = null;

        try {
            Communication.getInstance().send(new Request(Operation.GET_STUDENT_UNSELECTED_COURSES,student));

            Response response = Communication.getInstance().receive();

            if (response.getResponseType() == ResponseType.SUCCESS) {
                tempCourses = (List<Course>) response.getObject();
            }

        } catch (ClassNotFoundException | IOException ex) {
            JOptionPane.showMessageDialog(coursesView, "Error getting student's courses. Try again later!", "Error", JOptionPane.ERROR_MESSAGE);
            coursesView.dispose();
        }

        return tempCourses;
    }
}
