package com.luni.views.list;

import com.luni.connection.ConnectionManager;
import com.luni.data.entity.CollegeInfo;
import com.luni.data.service.CrmService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "")
@PageTitle("College Search | Compare Colleges")
public class ListView extends VerticalLayout {
    TextField filterText = new TextField();
    TextField filterLocText = new TextField();
    CheckboxGroup<String> checkboxSizeGroup = new CheckboxGroup<>();
    CheckboxGroup<String> checkboxTuitionGroup = new CheckboxGroup<>();
    CheckboxGroup<String> checkboxACTGroup = new CheckboxGroup<>();
    CrmService service;

    VerticalLayout snapsContainer = new VerticalLayout();
    List<HorizontalLayout> uniSnaps;
   

    public ListView() {
        this.service = new CrmService();
        ConnectionManager.loadAPI_Key();
        initializeHomePage(service);
    }

    private void initializeHomePage(CrmService service){
        Image img = new Image("images/luni.png", "banner logo");
        img.setWidth("100%");
        add(img);

        HorizontalLayout pageButtonLayout = new HorizontalLayout();


        Button button = new Button("Comparison Page");
        button.addClickListener(clickEvent -> {
            clearPage();
            initializeComparisonPage(service);
        });
        pageButtonLayout.add(button);

        Button costComparebutton = new Button("Cost Analysis Page");
        costComparebutton.addClickListener(clickEvent -> {
            clearPage();
            initializeCostAnalysisPage(service);
        });
        pageButtonLayout.add(costComparebutton);

        add(pageButtonLayout);

        snapsContainer.setHeightFull();


//        roseImage.setSrc("images/rose.png");
//        roseImage.setWidth("300px");
//        roseImage.setHeightFull();
//        tuImage.setSrc("images/tu.jpg");
//        tuImage.setWidth("300px");
//        tuImage.setHeightFull();
//        HorizontalLayout collegePics = new HorizontalLayout(roseImage, tuImage);

        //add(getToolbar(), getContent(), getFilters());



        this.service = service;
//        this.uniSnaps = getUniSnaps();
//        for(HorizontalLayout layout : this.uniSnaps){
//            snapsContainer.add(layout);
//        }

        //add(snapsContainer);
        add(renderContent());
        addClassName("list-view");
        setSizeFull();

//        add(collegePics, getToolbar(), getContent());
//        updateList();
    }

    private void initializeComparisonPage(CrmService service){
        Image img = new Image("images/luni.png", "banner logo");
        img.setWidth("100%");
        add(img);
        Button button = new Button("Home Page");
        button.addClickListener(clickEvent -> {
            clearPage();
            initializeHomePage(service);

        });
        add(button);
        add(renderComparisonContent());
    }

    private void initializeCostAnalysisPage(CrmService service){
        Image img = new Image("images/luni.png", "banner logo");
        img.setWidth("100%");
        add(img);
        Button button = new Button("Home Page");
        button.addClickListener(clickEvent -> {
            clearPage();
            initializeHomePage(service);

        });
        add(button);
        add(renderSimilarSchoolContent());
    }

    private void clearPage(){
        this.removeAll();
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout();
        content.setFlexGrow(2);
        content.setFlexGrow(1);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }


//    private List<HorizontalLayout> getUniSnaps() {
//        List<CollegeInfo> colleges = service.getCollegeInfos();
//        return toSnaps(colleges);
//    }

    public static List<HorizontalLayout> toSnaps(List<CollegeInfo> collegeInfos){
        final List<HorizontalLayout> list = new ArrayList<>();
        HorizontalLayout uniSnaps = new HorizontalLayout();
        uniSnaps.addClassName("uniSnaps");
        int numAcross = 4;
        int current = 1;
        for(CollegeInfo college : collegeInfos){
            UniSnip uniSnip = new UniSnip(college);
            uniSnip.addClickListener(new ComponentEventListener<ClickEvent<VerticalLayout>>() {
                @Override
                public void onComponentEvent(ClickEvent<VerticalLayout> verticalLayoutClickEvent) {
                    CollegeInfo copyCollege = uniSnip.getCollegeInfo();
                    CompareSnip cs = new CompareSnip(copyCollege);
                    for(HorizontalLayout layout : list){
                        int index = layout.indexOf(uniSnip);
                        if(index != -1){
                            layout.remove(uniSnip);
                            layout.addComponentAtIndex(index, cs);
                            break;
                        }
                    }


                    cs.addClickListener(new ComponentEventListener<ClickEvent<VerticalLayout>>() {
                        @Override
                        public void onComponentEvent(ClickEvent<VerticalLayout> verticalLayoutClickEvent) {
                            for(HorizontalLayout layout : list) {
                                int index = layout.indexOf(cs);
                                if(index != -1){
                                    layout.remove(cs);
                                    layout.addComponentAtIndex(index, uniSnip);
                                    break;
                                }
                            }
                        }
                    });
                }
            });
            uniSnaps.add(uniSnip);
            if(current % numAcross == 0){
                list.add(uniSnaps);
                uniSnaps = new HorizontalLayout();
                uniSnaps.addClassName("uniSnaps");
            }
            current++;
        }
        list.add(uniSnaps);
        return list;
    }

    private HorizontalLayout getToolbar() {

        Button searchButton = new Button("Search");
        searchButton.addClickListener(clickEvent -> {
           clearUniSnaps();
           String nameSearch = filterText.getValue();
           String locSearch = filterLocText.getValue();
           List<CollegeInfo> collegeInfos = new ArrayList<>();
           if(!nameSearch.isEmpty()){
               collegeInfos = service.getCollegeInfosByName(nameSearch, -1);
           }
           else if(!locSearch.isEmpty()){
               collegeInfos = service.getCollegeInfosByLoc(locSearch);
           }
           else if(!checkboxSizeGroup.getSelectedItems().isEmpty()){
               int[] minMax = getMinMax(checkboxSizeGroup);
               collegeInfos = service.getCollegeInfosBySize(minMax[0], minMax[1]);
           }
           else if(!checkboxTuitionGroup.getSelectedItems().isEmpty()){
               boolean inState = checkboxTuitionGroup.getSelectedItems().contains("In state?");
               int[] minMax = getMinMax(checkboxTuitionGroup);
               collegeInfos = service.getCollegeInfosByCost(minMax[0], minMax[1], inState);
           }
           else if(!checkboxACTGroup.getSelectedItems().isEmpty()){
               int[] minMax = getMinMax(checkboxACTGroup);
               collegeInfos = service.getCollegeInfosByACT(minMax[0], minMax[1]);
           }
           this.uniSnaps = toSnaps(collegeInfos);
           addUniSnaps(this.uniSnaps);
        });

        Button clearButton = new Button("Clear Search");
        clearButton.addClickListener(clickEvent -> {
            filterText.clear();
            filterLocText.clear();
            checkboxSizeGroup.clear();
            checkboxTuitionGroup.clear();
            checkboxACTGroup.clear();
        });

        HorizontalLayout toolbar = new HorizontalLayout(searchButton, clearButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }
    
    private HorizontalLayout renderContent() {
        HorizontalLayout content = new HorizontalLayout(getFilters(), snapsContainer);
        content.addClassName("mainpage");
        return content;
    }
    
    private VerticalLayout getFilters() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
//        filterText.addValueChangeListener(e -> updateList());
    	
    	filterLocText.setPlaceholder("Filter by location...");
        filterLocText.setClearButtonVisible(true);
        filterLocText.setValueChangeMode(ValueChangeMode.LAZY);
//        filterLocText.addValueChangeListener(e -> updateList());
        
        checkboxSizeGroup.setLabel("Size");
        checkboxSizeGroup.setItems("0-499", "500-999", "1000-4999",
                "5000-14999", "15000-34999", "35000-54999");
        //checkboxSizeGroup.select("Order ID", "Customer");
        checkboxSizeGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        checkboxTuitionGroup.setLabel("Tuition Per Semester");
        checkboxTuitionGroup.setItems("In state?", "0-499", "500-999", "1000-4999",
                "5000-14999", "15000-19999", "20000-25000");
        checkboxTuitionGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

        checkboxACTGroup.setLabel("Cumulative ACT Score Midpoint");
        checkboxACTGroup.setItems("20-24", "25-29", "30-32", "33-34", "35-36");
        checkboxACTGroup.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);

    	VerticalLayout filters = new VerticalLayout(filterText, filterLocText, checkboxSizeGroup,checkboxTuitionGroup, checkboxACTGroup, getToolbar());
    	filters.addClassName("filters");
    	return filters;
    }

    private int[] getMinMax(CheckboxGroup<String> cbg){
        int[] minMax = new int[2];
        int min = 999999999;
        int max = -1;
        for(String s : cbg.getSelectedItems()){
            if(s.contains("+")){
                s = s.substring(0, s.length()-1);
                int sInt = Integer.parseInt(s);
                sInt = sInt * 1000;
                max = sInt;
            }
            if(s.contains("-")){
                String[] split = s.split("-");
                for(String sp : split){
                    int i = Integer.parseInt(sp);
                    if(i < min){
                        min = i;
                    }
                    if(i > max){
                        max = i;
                    }
                }
            }
        }
        minMax[0] = min;
        minMax[1]= max;
        return minMax;
    }
    
    private void clearUniSnaps(){
        this.snapsContainer.removeAll();
    }

    private void addUniSnaps(List<HorizontalLayout> layouts){
        for(HorizontalLayout layout : layouts){
            this.snapsContainer.add(layout);
        }
    }

//    private void updateList() {
//        grid.setItems(service.findAllContacts(filterText.getValue()));
//    }

    private HorizontalLayout renderComparisonContent(){
        HorizontalLayout comparisonLayout = new HorizontalLayout();
        VerticalLayout compare1 = new ComparisonColumn(service).getComparisonContent();
        VerticalLayout compare2 = new ComparisonColumn(service).getComparisonContent();

        comparisonLayout.add(compare1);
        comparisonLayout.add(compare2);
        return comparisonLayout;
    }

    private VerticalLayout renderSimilarSchoolContent(){

        return new RecommendView(service);
    }

    /**
     *
     *   public ListView() {
     *         setSpacing(false);
     *
     *         Image img = new Image("images/empty-plant.png", "placeholder plant");
     *         img.setWidth("200px");
     *         add(img);
     *
     *         add(new H2("This place intentionally left empty"));
     *         add(new Paragraph("It’s a place where you can grow your own UI 🤗"));
     *
     *         setSizeFull();
     *         setJustifyContentMode(JustifyContentMode.CENTER);
     *         setDefaultHorizontalComponentAlignment(Alignment.CENTER);
     *         getStyle().set("text-align", "center");
     *     }
     */
}