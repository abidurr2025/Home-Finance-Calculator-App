# Real Estate Finance Calculator Suite

[![Repository Size](https://img.shields.io/github/repo-size/abidurrahman2025/Real-Estate-Finance-Calculator-Suite)](https://github.com/abidurrahman2025/Real-Estate-Finance-Calculator-Suite)
[![Languages](https://img.shields.io/badge/Lang-HTML%20%7C%20Java%20%7C%20Python-lightgrey.svg)](https://github.com/abidurrahman2025/Real-Estate-Finance-Calculator-Suite)

A polished, modular collection of real-estate finance calculators — web UI, Java utilities, and Python analysis scripts — designed for investors, agents, and developers who need accurate, reproducible property financial modeling.

Why this project exists
- Real estate investing requires repeated financial calculations (mortgages, cash flow, ROI, IRR, cap rates, amortization). This repo centralizes common calculators and tools into a single, extensible suite.
- Built to be accessible: an HTML front-end for quick use, Java components for production-ready integration, and Python scripts for data analysis and automation.

Repository language composition
- HTML: 63.7%
- Java: 23.0%
- Python: 13.3%

Table of contents
- Features
- Quick demo (examples)
- Getting started
- Folder layout (recommended)
- How to run (HTML, Java, Python)
- Example calculations
- Contributing
- Roadmap
- License & contact
- Frequently asked questions

Features
- Mortgage amortization (monthly payment, amortization schedule with principal/interest breakdown)
- Cash flow and NOI (net operating income)
- Cap rate and Gross Rent Multiplier (GRM)
- Cash-on-cash return
- Internal Rate of Return (IRR) and NPV calculators for multi-period cash flows
- Refinance and break-even analysis
- Comparative investment scenario tool (side-by-side)
- Clean, responsive HTML UI for quick checks + reusable Java & Python modules for integration and analysis

Quick demo — sample calculations
- Mortgage monthly payment (annuity formula)
  - Inputs: principal $P, annual interest rate r (as percent), term years n
  - Monthly rate: i = r / 12
  - Payments: M = P * i * (1+i)^(12n) / ((1+i)^(12n) - 1)
  - Example: P = $300,000, r = 4.0%, n = 30 -> M ≈ $1,432.25

- Cap rate
  - CapRate = NetOperatingIncome / PurchasePrice
  - Example: NOI = $28,000, Price = $400,000 -> CapRate = 7%

Getting started (local)
1. Clone the repo
   ```
   git clone https://github.com/abidurrahman2025/Real-Estate-Finance-Calculator-Suite.git
   cd Real-Estate-Finance-Calculator-Suite
   ```

2. Quick web UI (fastest)
   - Open the HTML front-end in your browser:
     - Open `web/index.html` (or `index.html` at project root) in any modern browser.
   - No server required for basic calculators.

3. Java components
   - If the Java code uses Maven:
     ```
     cd java
     mvn clean package
     # run jar if present
     java -jar target/real-estate-calculators.jar
     ```
   - If no build tool is present, compile with javac:
     ```
     javac -d out src/main/java/com/realestate/**/*.java
     java -cp out com.realestate.Main
     ```
   - The Java modules are designed for integration into backend services; they expose calculator classes with simple method signatures (e.g., MortgageCalculator.computeMonthlyPayment(...)).

4. Python scripts & analysis
   - Install dependencies (if any):
     ```
     python3 -m venv .venv
     source .venv/bin/activate
     pip install -r python/requirements.txt
     ```
   - Run an analysis script:
     ```
     python python/irrcalc.py --cashflows cashflows.csv
     ```
   - Python modules are ideal for batch processing, CSV import/export, and Jupyter notebook analysis.

Folder layout (recommended / example)
- web/ or public/ — HTML, CSS, and client-side JS (primary interactive UI)
- java/ — Java library and example CLI or server integration
- python/ — scripts, notebooks, data import utilities
- docs/ — design docs, formulas, test cases
- tests/ — unit tests for Java and Python modules
- examples/ — sample data and pre-filled demo scenarios

Usage examples (snippets)

- Mortgage (Python example)
```
from calculators.mortgage import monthly_payment
pmnt = monthly_payment(principal=300000, annual_rate=0.04, years=30)
print(f"Monthly Payment: ${pmnt:,.2f}")
```

- Java (conceptual usage)
```java
MortgageCalculator mc = new MortgageCalculator(300000, 0.04, 30);
double monthly = mc.getMonthlyPayment();
System.out.println("Monthly = " + monthly);
```

Testing & validation
- Unit tests for calculators are strongly recommended. If present:
  - Java: `mvn test` or `gradle test`
  - Python: `pytest tests/`
- Compare outputs with authoritative sources (bank amortization tables, Excel) to validate correctness.

Contributing
We welcome contributors.
1. Fork the repo.
2. Create a branch: `git checkout -b feat/your-feature`
3. Add tests and documentation for your change.
4. Open a pull request describing the change and motivation.

Suggested PR checklist
- [ ] Unit tests added/updated
- [ ] Linting passes (Java/Python/HTML)
- [ ] README updated (if functionality changes)
- [ ] Backward-compatible API changes

Coding style
- Java: follow standard conventions; include Javadoc for public methods.
- Python: follow PEP8; include docstrings and type hints where appropriate.
- HTML/CSS/JS: accessibility and responsive layout for calculators.

Roadmap (ideas)
- Add a REST API wrapper for Java calculators (Spring Boot example)
- Docker images for reproducible execution (web + API)
- Add more advanced financial models (sensitivity analysis, scenario Monte Carlo)
- Interactive Jupyter notebooks with data visualization (rental yield heatmaps)
- Localization (currencies, number formats, tax rules by country)

License
- Recommended: MIT License — simple and permissive. See LICENSE file.

Contact
- Maintainer: abidurrahman2025
- For questions, open an issue or start a discussion in this repository.

FAQ / Troubleshooting
Q: I opened the HTML and UI doesn't load correctly.
A: Ensure you opened the correct `index.html`, and if the page fetches a local API, run the backend services or serve the directory with a static server:
```
python -m http.server 8000
# then visit http://localhost:8000
```

Q: How can I trust the math?
A: The repo includes unit tests and formula references in `docs/` (if not present, please raise an issue). Always cross-check results against your financial advisor for decisions.

Acknowledgements
- Based on standard financial formulas (time value of money, annuity formulas).
- Contributions and ideas from the real estate investing community.

