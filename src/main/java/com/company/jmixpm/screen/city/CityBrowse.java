package com.company.jmixpm.screen.city;

import io.jmix.ui.screen.*;
import com.company.jmixpm.entity.City;

@UiController("City.browse")
@UiDescriptor("city-browse.xml")
@LookupComponent("table")
public class CityBrowse extends MasterDetailScreen<City> {
}