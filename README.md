
```
system
├─ docker-compose.yml
├─ Dockerfile
├─ pom.xml
└─ src
   ├─ main
   │  ├─ java
   │  │  └─ com
   │  │     └─ Banking
   │  │        └─ system
   │  │           ├─ config
   │  │           │  ├─ JpaAuditingConfig.java
   │  │           │  └─ SecurityConfig.java
   │  │           ├─ controller
   │  │           │  ├─ AccountController.java
   │  │           │  ├─ AdminController.java
   │  │           │  ├─ AuthController.java
   │  │           │  ├─ TransactionController.java
   │  │           │  └─ UserController.java
   │  │           ├─ dto
   │  │           │  ├─ request
   │  │           │  │  ├─ createAccountRequest.java
   │  │           │  │  ├─ DepositRequest.java
   │  │           │  │  ├─ LoginRequest.java
   │  │           │  │  ├─ RegisterRequest.java
   │  │           │  │  ├─ TransferRequest.java
   │  │           │  │  ├─ UpdateUserRequest.java
   │  │           │  │  └─ WithdrawRequest.java
   │  │           │  └─ response
   │  │           │     ├─ AccountResponse.java
   │  │           │     ├─ AdminDashboardResponse.java
   │  │           │     ├─ ApiResponse.java
   │  │           │     ├─ AuthResponse.java
   │  │           │     ├─ TransactionResponse.java
   │  │           │     └─ UserResponse.java
   │  │           ├─ entity
   │  │           │  ├─ Account.java
   │  │           │  ├─ Transaction.java
   │  │           │  └─ User.java
   │  │           ├─ enums
   │  │           │  ├─ AccountStatus.java
   │  │           │  ├─ AccountType.java
   │  │           │  ├─ Role.java
   │  │           │  ├─ TransactionStatus.java
   │  │           │  └─ TransactionType.java
   │  │           ├─ exception
   │  │           │  ├─ AccountNotActiveException.java
   │  │           │  ├─ DuplicateEmailException.java
   │  │           │  ├─ GlobalExceptionHandler.java
   │  │           │  ├─ InsufficientFundsException.java
   │  │           │  ├─ RateLimiterException.java
   │  │           │  ├─ ResourceNotFoundException.java
   │  │           │  └─ UnauthorizedAccessException.java
   │  │           ├─ repository
   │  │           │  ├─ AccountRepository.java
   │  │           │  ├─ TransactionRepository.java
   │  │           │  └─ UserRepository.java
   │  │           ├─ security
   │  │           │  ├─ JwtAuthenticationFilter.java
   │  │           │  ├─ JwtUtil.java
   │  │           │  ├─ LoginAttemptService.java
   │  │           │  └─ UserDetailsServiceImpl.java
   │  │           ├─ Service
   │  │           │  ├─ implementations
   │  │           │  │  ├─ AccountServiceImpl.java
   │  │           │  │  ├─ AdminServiceImpl.java
   │  │           │  │  ├─ AuthServiceImpl.java
   │  │           │  │  ├─ TransactionServiceImpl.java
   │  │           │  │  └─ UserServiceImpl.java
   │  │           │  └─ interfaces
   │  │           │     ├─ AccountService.java
   │  │           │     ├─ AdminService.java
   │  │           │     ├─ AuthService.java
   │  │           │     ├─ TransactionService.java
   │  │           │     └─ UserService.java
   │  │           └─ SystemApplication.java
   │  └─ resources
   │     ├─ application.properties
   │     ├─ static
   │     └─ templates
   └─ test
      └─ java
         └─ com
            └─ Banking
               └─ system
                  ├─ SystemApplicationTests.java
                  ├─ TestAccountServiceImpl
                  │  └─ AccountServiceImplTest.java
                  ├─ TestAdminServiceImpl
                  │  └─ AdminServiceImplTest.java
                  ├─ TestAuthService
                  │  └─ AuthServiceImplTest.java
                  ├─ TestTransactionServiceImpl
                  │  └─ TransactionServiceImplTest.java
                  └─ TestUserServiceImpl
                     └─ UserServiceImplTest.java

```