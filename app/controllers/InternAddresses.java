package controllers;

import play.*;
import play.mvc.*;

import models.InternAddress;

@With(Secure.class)
@CRUD.For(InternAddress.class)
public class InternAddresses extends CRUD {

}
