
# vat-registered-companies-frontend

The purpose of a VRN checker service is to help customers comply with their responsibilities for calculating VAT
liabilities by helping them ensure they are trading with registered third parties.

Service can be used in production at - https://www.tax.service.gov.uk/check-vat-number/enter-vat-details

# Run Services

You can run services locally through Service Manager:
```
sm2 --start VAT_REG_CO_ALL
```

To run repo on port 8730:
```
sbt run
```

## Testing and coverage

To run tests use:
```
sbt test
```

To run tests and coverage use:
```
sbt clean coverage test coverageReport
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
