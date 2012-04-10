package controllers;

import play.*;
import play.mvc.*;

import models.InternCity;

@Check("admin")
@With(Secure.class)
@CRUD.For(InternCity.class)
public class InternCities extends CRUD {

}
