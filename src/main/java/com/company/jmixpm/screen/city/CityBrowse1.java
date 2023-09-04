package com.company.jmixpm.screen.city;

import io.jmix.ui.screen.*;
import com.company.jmixpm.entity.City;

@UiController("City.browse1")
@UiDescriptor("city-browse1.xml")
@LookupComponent("citiesTable")
public class CityBrowse1 extends StandardLookup<City> {
}