Optimizely Skills Assesment
===================

This repository houses the Skills Assessment for Optmizely X FullStack
It comes with two experiments:  
A/B test for the menu of available cars: available_cars
Feature test for the Insurance offer: offer_insurance

## Getting Started
For forcing variation_2 use userTest
For whitelisting into offer_insurance use userInsurance

### QA the experiments
Pass the user userTest for whitelisting in the available_cars experiment.
Pass the user userInsurance to be forced into offer_insurance variation_2 60% discount

### How to run

#### Gradle

You can do a ```./gradlew``` build and run via run.sh.  
Optionally the user ID can be passed in the first parameter:
```./run.sh userTest```
