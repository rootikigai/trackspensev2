package ng.ikigai.trackspensev2.service;

import lombok.RequiredArgsConstructor;
import ng.ikigai.trackspensev2.dto.ExpenseDTO;
import ng.ikigai.trackspensev2.dto.IncomeDTO;
import ng.ikigai.trackspensev2.dto.RecentTransactionsDTO;
import ng.ikigai.trackspensev2.entity.ProfileEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData(){
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();
        List<RecentTransactionsDTO> recentTransactions = concat(latestIncomes.stream().map(income ->
                RecentTransactionsDTO.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .icon(income.getIcon())
                        .name(income.getName())
                        .amount(income.getAmount())
                        .type("income")
                        .date(income.getDate())
                        .createdAt(income.getCreatedAt())
                        .updatedAt(income.getUpdatedAt())
                        .build()),
               latestExpenses.stream().map(expense ->
                RecentTransactionsDTO.builder()
                        .id(expense.getId())
                        .profileId(profile.getId())
                        .icon(expense.getIcon())
                        .name(expense.getName())
                        .amount(expense.getAmount())
                        .type("expense")
                        .date(expense.getDate())
                        .createdAt(expense.getCreatedAt())
                        .updatedAt(expense.getUpdatedAt())
                        .build()))
                .sorted((RecentTransactionsDTO a, RecentTransactionsDTO b) -> {
                    int cmp = b.getDate().compareTo(a.getDate());
                    if(cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null){
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
                }).collect(Collectors.toList());
        returnValue.put("totalBalance",
                incomeService.getTotalIncomesForCurrentUser()
                        .subtract(expenseService.getTotalExpenseForCurrentUser()));
        returnValue.put("totalIncome", incomeService.getTotalIncomesForCurrentUser());
        returnValue.put("totalExpense", expenseService.getTotalExpenseForCurrentUser());
        returnValue.put("recent5Incomes", incomeService.getLatest5IncomesForCurrentUser());
        returnValue.put("recent5Expenses", expenseService.getLatest5ExpensesForCurrentUser());
        returnValue.put("recentTransactions", recentTransactions);
        return returnValue;
    }
}
