package ng.ikigai.trackspensev2.controller;

import lombok.RequiredArgsConstructor;
import ng.ikigai.trackspensev2.dto.ExpenseDTO;
import ng.ikigai.trackspensev2.dto.FilterDTO;
import ng.ikigai.trackspensev2.dto.IncomeDTO;
import ng.ikigai.trackspensev2.service.ExpenseService;
import ng.ikigai.trackspensev2.service.IncomeService;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filter){
        //Preparing the data or validation
        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortField() != null ? filter.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);
        if("income".equals(filter.getType())){
            List<IncomeDTO> sortedIncomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(sortedIncomes);
        } else if ("expense".equals(filter.getType())){
            List<ExpenseDTO> sortedExpenses = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(sortedExpenses);
        }
        else{
            return ResponseEntity.badRequest().body("Invalid type. Must be 'income' or 'expense'");
        }
    }
}
